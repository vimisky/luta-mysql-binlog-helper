package io.github.vimisky.luta.mysql.binlog.helper.replicator;

import com.alibaba.druid.pool.DruidDataSource;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogChannel;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer.BinlogRowDataDeserializer;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer.DebeziumRowDeserializers;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogPositionEntity;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogFilterService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogPositionService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.SchemaCacheService;
import io.github.vimisky.luta.mysql.binlog.helper.service.LutaBinlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class BinlogReplicator {
    private static final Logger logger = LoggerFactory.getLogger(BinlogReplicator.class);

    private String taskUUID;

    private LutaBinlogService lutaBinlogService;
    private BinlogStatusService binlogStatusService;

    private boolean running = false;

    BlockingQueue<Event> binlogEventIncomingQueue = null;
//    BlockingQueue<String> transactionSqlOutputQueue = null;

    private BinaryLogClient binaryLogClient = null;
    private BinlogEventListenerImpl runningBinlogEventListener = null;
    private BinlogEventProcessor binlogEventProcessor = null;
//    private BinlogTransactionOutput binlogTransactionOutput = null;

//    public BinlogReplicator() {
//    }

    public BinlogReplicator(String taskUUID, LutaBinlogService lutaBinlogService) {
        this.taskUUID = taskUUID;
        this.lutaBinlogService = lutaBinlogService;
        this.binlogStatusService = new BinlogStatusService(taskUUID, lutaBinlogService);
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public LutaBinlogService getLutaBinlogService() {
        return lutaBinlogService;
    }

    public void setLutaBinlogService(LutaBinlogService lutaBinlogService) {
        this.lutaBinlogService = lutaBinlogService;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public BinaryLogClient getBinaryLogClient() {
        return binaryLogClient;
    }

    public void setBinaryLogClient(BinaryLogClient binaryLogClient) {
        this.binaryLogClient = binaryLogClient;
    }

    public BinlogEventProcessor getBinlogEventProcessor() {
        return binlogEventProcessor;
    }

    public void setBinlogEventProcessor(BinlogEventProcessor binlogEventProcessor) {
        this.binlogEventProcessor = binlogEventProcessor;
    }

//    public BinlogTransactionOutput getBinlogTransactionOutput() {
//        return binlogTransactionOutput;
//    }
//
//    public void setBinlogTransactionOutput(BinlogTransactionOutput binlogTransactionOutput) {
//        this.binlogTransactionOutput = binlogTransactionOutput;
//    }
protected EventDeserializer createEventDeserializer() {
    final Map<Long, TableMapEventData> tableMapEventByTableId = new HashMap<>();
    //重载nextEvent，获取tableMapEvent Map，用于Deserializer构造函数使用。
    EventDeserializer eventDeserializer = new EventDeserializer() {
        @Override
        public Event nextEvent(ByteArrayInputStream inputStream) throws IOException {
            Event event = super.nextEvent(inputStream);
                // We have to record the most recent TableMapEventData for each table number for our custom deserializers ...
            if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
                TableMapEventData tableMapEvent = event.getData();
                tableMapEventByTableId.put(tableMapEvent.getTableId(), tableMapEvent);
            }
            if (event.getHeader().getEventType() == EventType.ROTATE && event.getHeader().getTimestamp() != 0) {
                tableMapEventByTableId.clear();
            }
            return event;
        }
    };

    eventDeserializer.setEventDataDeserializer(EventType.WRITE_ROWS,
            new DebeziumRowDeserializers.WriteRowsDeserializer(tableMapEventByTableId));
    eventDeserializer.setEventDataDeserializer(EventType.UPDATE_ROWS,
            new DebeziumRowDeserializers.UpdateRowsDeserializer(tableMapEventByTableId));
    eventDeserializer.setEventDataDeserializer(EventType.DELETE_ROWS,
            new DebeziumRowDeserializers.DeleteRowsDeserializer(tableMapEventByTableId));
    eventDeserializer.setEventDataDeserializer(EventType.EXT_WRITE_ROWS,
            new DebeziumRowDeserializers.WriteRowsDeserializer(
                    tableMapEventByTableId).setMayContainExtraInformation(true));
    eventDeserializer.setEventDataDeserializer(EventType.EXT_UPDATE_ROWS,
            new DebeziumRowDeserializers.UpdateRowsDeserializer(
                    tableMapEventByTableId).setMayContainExtraInformation(true));
    eventDeserializer.setEventDataDeserializer(EventType.EXT_DELETE_ROWS,
            new DebeziumRowDeserializers.DeleteRowsDeserializer(
                    tableMapEventByTableId).setMayContainExtraInformation(true));

    return eventDeserializer;
}

    public boolean start(){
        String srcHost = "";
        Integer srcPort = 3306;
        String srcUsername = "";
        String srcPassword = "";

        String binlogFilename = "";
        Long binlogNextPosition = 0L;
        String remoteMysqlTimezone = null;

        String dstHost = "";
        Integer dstPort = 5672;
        String dstVhost = "";
        String dstUsername = "";
        String dstPassword = "";

        LutaBinlogTask lutaBinlogTask = lutaBinlogService.getBinlogTask(taskUUID);
        LutaBinlogChannel lutaBinlogChannel = lutaBinlogTask.getLutaBinlogChannel();

        BinlogPositionService binlogPositionService = new BinlogPositionService(taskUUID, lutaBinlogService);


        //初始化内存队列
        this.binlogEventIncomingQueue = new LinkedBlockingQueue<>(100);
//        this.transactionSqlOutputQueue = new LinkedBlockingQueue<>(100);

        //初始化RabbitMQ输出
        BinlogTransactionOutput binlogTransactionOutput = null;
        CachingConnectionFactory connectionFactory = null;
        RabbitTemplate outputRabbitTemplate = null;

        dstHost = lutaBinlogChannel.getDstHost();
        dstPort = lutaBinlogChannel.getDstPort();
        dstVhost = lutaBinlogChannel.getDstVhost();
        dstUsername = lutaBinlogChannel.getDstUsername();
        dstPassword = lutaBinlogChannel.getDstPassword();

//        try{
        //初始化RabbitMQ模板
        connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(dstHost);
        connectionFactory.setPort(dstPort);
        connectionFactory.setVirtualHost(dstVhost);
        connectionFactory.setUsername(dstUsername);
        connectionFactory.setPassword(dstPassword);

        outputRabbitTemplate = new RabbitTemplate(connectionFactory);
        outputRabbitTemplate.setExchange(lutaBinlogChannel.getDstTopicName());
        outputRabbitTemplate.setRoutingKey(lutaBinlogChannel.getDstRoutingKey());

        BinlogOutput binlogOutput = new BinlogRabbitMQOutput(outputRabbitTemplate);

            //启动RabbitMQ输出
//            binlogTransactionOutput = new BinlogTransactionOutput(transactionSqlOutputQueue, outputRabbitTemplate);
//            binlogTransactionOutput.setBinlogStatusService(this.binlogStatusService);
//            this.binlogTransactionOutput = binlogTransactionOutput;
//            Thread thread1 = new Thread(binlogTransactionOutput::start);
//            thread1.start();

//        }catch (Exception e){
//            e.printStackTrace();
//            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
//                    "BinlogTransactionOutput启动遇到异常");
//            return false;
//        }

        //初始化MySQL Schema缓存服务
        SchemaCacheService schemaCacheService = null;
        //初始化MySQL jdbcTemplate
        JdbcTemplate jdbcTemplate = null;
        srcHost = lutaBinlogChannel.getSrcHost();
        srcPort = lutaBinlogChannel.getSrcPort();
        srcUsername = lutaBinlogChannel.getSrcUsername();
        srcPassword = lutaBinlogChannel.getSrcPassword();

        try{
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(lutaBinlogChannel.getSrcDriverClassName());
            druidDataSource.setUrl("jdbc:mysql://" + srcHost + ":" + srcPort );
            druidDataSource.setUsername(srcUsername);
            druidDataSource.setPassword(srcPassword);
            jdbcTemplate = new JdbcTemplate(druidDataSource);

            schemaCacheService = new SchemaCacheService(jdbcTemplate);
            logger.info("schemaCacheService初始化完成");
        }catch(Exception e){
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "schemaCacheService初始化遇到异常");
            return false;
        }

        /*
         * 初始化Binlog Client
         * */

        //初始化Binlog监听器
        BinaryLogClient.EventListener binlogEventListener = new BinlogEventListenerImpl(binlogEventIncomingQueue);
        BinlogEventListenerImpl binlogEventListenerImpl = (BinlogEventListenerImpl) binlogEventListener;
        binlogEventListenerImpl.setBinlogStatusService(binlogStatusService);
        this.runningBinlogEventListener = (BinlogEventListenerImpl)binlogEventListener;
        //初始化Binlog Client
        BinaryLogClient binaryLogClient = new BinaryLogClient(srcHost, srcPort, srcUsername, srcPassword);
        this.binaryLogClient = binaryLogClient;

        //过期
        //使用Debezium事件解析，不需要设置这个了。
//        EventDeserializer eventDeserializer = new EventDeserializer();

//        eventDeserializer.setCompatibilityMode(
//                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
//        );
        //使用Debezium事件解析器
        EventDeserializer eventDeserializer = createEventDeserializer();
        eventDeserializer.setCompatibilityMode(EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
        binaryLogClient.setEventDeserializer(eventDeserializer);

        binaryLogClient.registerEventListener(binlogEventListener);
//        binaryLogClient.setBlocking(true);

        BinlogPositionEntity binlogPositionEntity = binlogPositionService.getNextPosition();

        binlogFilename = binlogPositionEntity.getBinlogFilename();
        binlogNextPosition = binlogPositionEntity.getBinlogPosition();
        binaryLogClient.setBinlogFilename(binlogFilename);
        binaryLogClient.setBinlogPosition(binlogNextPosition);
//        binaryLogClient.setServerId(serverId);
        try {
            binaryLogClient.connect(3000);
            logger.info("Binlog客户端连接完成");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "Binlog客户端连接遇到异常");
            return false;
        }

        //Event处理器
        //初始化Binlog Event处理器
        remoteMysqlTimezone = lutaBinlogChannel.getRemoteMysqlTimezone();
        BinlogRowDataDeserializer binlogRowDataDeserializer = new BinlogRowDataDeserializer();
        binlogRowDataDeserializer.setTimezoneID(remoteMysqlTimezone);

        BinlogFilterService binlogFilterService = new BinlogFilterService(taskUUID, lutaBinlogService);
        BinlogEventProcessor binlogEventProcessor = new BinlogEventProcessor();
        this.binlogEventProcessor = binlogEventProcessor;
        binlogEventProcessor.setBinlogEventIncomingQueue(binlogEventIncomingQueue);
//        binlogEventProcessor.setTransactionSqlOutputQueue(transactionSqlOutputQueue);
        binlogEventProcessor.setSchemaCacheService(schemaCacheService);
        binlogEventProcessor.setBinlogRowDataDeserializer(binlogRowDataDeserializer);
        binlogEventProcessor.setBinlogPositionService(binlogPositionService);
        binlogEventProcessor.setBinlogFilterService(binlogFilterService);
        binlogEventProcessor.setBinlogStatusService(this.binlogStatusService);
        binlogEventProcessor.setBinlogOutput(binlogOutput);

        try{
            Thread thread2 = new Thread(binlogEventProcessor::start);
            thread2.start();
        }catch(Exception e){
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "EventProcessor启动遇到异常");
            return false;
        }

        this.setRunning(true);
        binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_START);
        return true;
    }

    public boolean stop(){
        //stop环节，就不设置各个成员变量为null了，避免停止失败需要再次触发停止按钮。
        //停掉BinaryLogClient
        try {
            logger.info("<before disconnect> BinaryLogClient isKeepAlive: " + this.binaryLogClient.isKeepAlive() +
                    " isConnected: " + this.binaryLogClient.isConnected());
            this.binaryLogClient.unregisterEventListener(this.runningBinlogEventListener);
            this.runningBinlogEventListener.setStopFlag(true);
            this.binaryLogClient.disconnect();
            logger.info("<after disconnect>: BinaryLogClient isKeepAlive: " + this.binaryLogClient.isKeepAlive() +
                    " isConnected: " + this.binaryLogClient.isConnected());

        } catch (IOException e) {
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "停止Binlog客户端遇到异常");
            return false;
        }
        try {
            //停掉EventProcessor
            if (this.binlogEventProcessor!=null) this.binlogEventProcessor.setStopFlag(true);
        }catch(Exception e){
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "停止EventProcessor遇到异常");
            return false;
        }

//        try {
//            //停掉Output
//            if (this.binlogTransactionOutput!=null) this.binlogTransactionOutput.setStopFlag(true);
//        }catch(Exception e){
//            e.printStackTrace();
//            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
//                    "停止binlogTransactionOutput遇到异常");
//            return false;
//        }

        this.setRunning(false);
        binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_STOP);
        return true;
    }

    public String getStatus(){
        String status = "";
        boolean isBinaryLogClientConnected = this.binaryLogClient.isConnected();
        status += "BinaryLogClient isConnected: " + isBinaryLogClientConnected + "; ";
        status += this.binlogEventProcessor.getStatus();
//        status += this.binlogTransactionOutput.getStatus();
        return status;
    }

    public void update(){

    }

}

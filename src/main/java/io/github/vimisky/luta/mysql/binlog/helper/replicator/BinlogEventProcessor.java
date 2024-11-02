package io.github.vimisky.luta.mysql.binlog.helper.replicator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.shyiko.mysql.binlog.event.*;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.deserializer.BinlogRowDataDeserializer;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogPositionEntity;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogTransaction;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.SchemaColumnDef;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.SchemaTableDef;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogFilterService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogPositionService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.SchemaCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Event不等同于读取Binlog，只有和读取binlog相关的Event才会包含正确的读取Binlog相关的Position。
 * 跟读取Binlog无关的一些Event也有Position字段，但是值为0，我猜测是因为数据结构。
 * **/

public class BinlogEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BinlogEventProcessor.class);

    private boolean stopFlag = false;

    private BlockingQueue<Event> binlogEventIncomingQueue;
    private BlockingQueue<String> transactionSqlOutputQueue;

    private BinlogPositionService binlogPositionService;
    private BinlogStatusService binlogStatusService;
    private BinlogFilterService binlogFilterService;
    private SchemaCacheService schemaCacheService;
    private BinlogOutput binlogOutput;
//    private BinlogEventFilter binlogEventFilter;

    private BinlogRowDataDeserializer binlogRowDataDeserializer;

    private Event currentEvent, lastEvent;
    private BinlogPositionEntity binlogNextPositionEntity = new BinlogPositionEntity();

    //1表示持久化position的条件满足了
    private Integer binlogCommitFlag = 0;
    private StringBuffer txStringBuffer = new StringBuffer();
    private BinlogTransaction binlogTransaction = new BinlogTransaction();
    private Integer binlogCommitCountLimit = 500;

    public boolean isStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public Event getLastEvent() {
        return lastEvent;
    }

    public BinlogPositionEntity getBinlogNextPositionEntity() {
        return binlogNextPositionEntity;
    }

    public BlockingQueue<Event> getBinlogEventIncomingQueue() {
        return binlogEventIncomingQueue;
    }

    public void setBinlogEventIncomingQueue(BlockingQueue<Event> binlogEventIncomingQueue) {
        this.binlogEventIncomingQueue = binlogEventIncomingQueue;
    }

    public BlockingQueue<String> getTransactionSqlOutputQueue() {
        return transactionSqlOutputQueue;
    }

    public void setTransactionSqlOutputQueue(BlockingQueue<String> transactionSqlOutputQueue) {
        this.transactionSqlOutputQueue = transactionSqlOutputQueue;
    }

    public BinlogPositionService getBinlogPositionService() {
        return binlogPositionService;
    }

    public void setBinlogPositionService(BinlogPositionService binlogPositionService) {
        this.binlogPositionService = binlogPositionService;
    }

    public BinlogStatusService getBinlogStatusService() {
        return binlogStatusService;
    }

    public void setBinlogStatusService(BinlogStatusService binlogStatusService) {
        this.binlogStatusService = binlogStatusService;
    }

    public BinlogFilterService getBinlogFilterService() {
        return binlogFilterService;
    }

    public void setBinlogFilterService(BinlogFilterService binlogFilterService) {
        this.binlogFilterService = binlogFilterService;
    }

    public BinlogOutput getBinlogOutput() {
        return binlogOutput;
    }

    public void setBinlogOutput(BinlogOutput binlogOutput) {
        this.binlogOutput = binlogOutput;
    }

    public SchemaCacheService getSchemaCacheService() {
        return schemaCacheService;
    }

    public void setSchemaCacheService(SchemaCacheService schemaCacheService) {
        this.schemaCacheService = schemaCacheService;
    }

    public BinlogRowDataDeserializer getBinlogRowDataDeserializer() {
        return binlogRowDataDeserializer;
    }

    public void setBinlogRowDataDeserializer(BinlogRowDataDeserializer binlogRowDataDeserializer) {
        this.binlogRowDataDeserializer = binlogRowDataDeserializer;
    }
    public String getStatus(){
        String status = "stopFlag: " + this.stopFlag + ";";
        if (this.currentEvent != null){
            status += "EventProcessor currentEvent timestamp: " + new Date(this.currentEvent.getHeader().getTimestamp()) + ";";
        }
        return status;
    }
    //Header:都包含nextposition字段
    //Data: ROTATE事件包含binlogfilename和binlogposition字段。
    //ROTATE,QUERY,XID会保存position。其他情况不需要，可replay。
    public void start(){
        logger.info("start: 开始监听Event队列并处理Event");
        while(!this.stopFlag){
//            logger.trace("while:开始处理下一条Event");
            try {
                Event event = null;
                if(this.currentEvent!=null) {
                    Thread.sleep(10000);
                    logger.info("this.currentEvent不为null，重新处理");
                    event = this.currentEvent;
                }else{
                    event = binlogEventIncomingQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if (event == null){
                        logger.trace("从队列中获取Binlog事件超时，继续poll");
                        continue;
                    }else {
                        this.currentEvent = event;
                    }
                }
                logger.debug("从队列中获取到Binlog新事件，当前系统时间戳: " + System.currentTimeMillis());
                //获取事件Header并打印
                EventHeaderV4 eventHeader = event.getHeader();
                EventType eventType = eventHeader.getEventType();
                logger.debug("EventType: " + eventType);
                logger.debug("EventHeader: " + eventHeader.toString());
                //获取事件Data并打印
                EventData data = event.getData();
                if (data != null)
                    logger.debug("EventData: "+data.toString());
                else
                    logger.debug("EventData: null");

                //EventHeader对象，get返回的position和NextPostion,是当前position和下一个position。
                //EventHeader对象，打印的话，只有NextPosition
                //根据Event长度，NextPosition值，可以算出来Position的值。所以就有了这个getPosition和getNextPosition方法。
//                logger.info("当前Event中的event长度:"+eventHeader.getEventLength());
//                logger.info("当前Event中的header长度:"+eventHeader.getHeaderLength());
//                logger.info("当前Event中的data长度:"+eventHeader.getDataLength());
                //不是所有event里都有position
//                logger.info("Binlog-Position:"+eventHeader.getPosition()+" NextPosition:"+eventHeader.getNextPosition());

                logger.debug("开始根据EventType处理Event");
                switch (eventType){
                    case QUERY:
                        this.processQueryEvent(event);
                        break;
                    case TABLE_MAP:
                        this.processTableMapEvent(event);
                        break;
                    case WRITE_ROWS:
                    case EXT_WRITE_ROWS:
                        this.processWriteRowsEvent(event);
                        break;
                    case UPDATE_ROWS:
                    case EXT_UPDATE_ROWS:
                        this.processUpdateRowsEvent(event);
                        break;
                    case DELETE_ROWS:
                    case EXT_DELETE_ROWS:
                        this.processDeleteRowsEvent(event);
                        break;
                    case XID:
                        this.processXIDEvent(event);
                        break;
                    case ROTATE:
                        this.processRotateEvent(event);
                        break;
                    default:
                        this.processOtherEvent(event);
                        break;
                }
                this.lastEvent = this.currentEvent;
                this.currentEvent = null;
                logger.debug("Event处理完成, FileName: " + this.binlogNextPositionEntity.getBinlogFilename() + ", NextPosition: " +this.binlogNextPositionEntity.getBinlogPosition());
            } catch (Exception e) {
                e.printStackTrace();
                String eventHeaderString = "";
                if (this.currentEvent != null){
                    EventHeaderV4 eventHeader = this.currentEvent.getHeader();
                    eventHeaderString = eventHeader.toString();
                }
                logger.error("处理Event遇到异常, Event Header: "+ eventHeaderString);
                this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                        "处理Event遇到异常, Event Header: "+ eventHeaderString + "; 报错信息： " + e.getMessage());
            }
        }
    }

    public void processOtherEvent(Event event){
        logger.debug("非关注事件，不做处理");
    }
    public void processRotateEvent(Event event){
        logger.debug("ROTATE事件，持久化Binlog Position");
        EventHeaderV4 eventHeader = event.getHeader();
        RotateEventData eventData = (RotateEventData)event.getData();
//        binlogTableMapService.reload();
        //ROTATE事件，特殊处理，因为里面包含了新的binlog filename

        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogFilename(eventData.getBinlogFilename());
        this.binlogNextPositionEntity.setBinlogPosition(eventData.getBinlogPosition());

//        binlogPositionService.saveNextPosition(this.binlogNextPositionEntity);
    }
    public void processTableMapEvent(Event event) throws SQLException {
        logger.debug("TableMap事件，刷新TableMapCache");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());

        TableMapEventData tableMapEventData = (TableMapEventData) event.getData();
        String databaseName = tableMapEventData.getDatabase();
        String tableName = tableMapEventData.getTable();
        long tableId = tableMapEventData.getTableId();
        //tableId并不是固定不变的，mysql cache中如果过期清理了，再次载入table metadata时，会重新分配一个新的tableId。
        //所以不能按照tableId来做索引，而是应该保存当前操作的table信息，由于1个事务中可能不止操作一张表，所以应该保存当前操作的tableList信息。
        //每次一查，肯定没问题。要不要缓存这个table metadata信息呢？ 如果缓存，更新触发条件是什么？
        // 如果处理binlog时，也处理DDL语句，那么可以将DDL语句作为触发条件
        // 将ROTATE事件作为条件
//            logger.info("event database name" + databaseName + " , table name: " + tableName + " , tableId: " + tableId);
//        logger.info("命中TableMapEvent: data: " + tableMapEventData.toString());
        schemaCacheService.syncTableMetaData(tableId,databaseName,tableName);

//        try {
//            binlogTableMapService.syncTableMetaData(tableId,databaseName,tableName);
//        } catch (SQLException e) {
//            //如果同步表字段失败，则退出程序。
//            e.printStackTrace();
////            logger.info("同步表字段失败，开始重启应用");
////            ToolkitApplication.restart();
//        }
    }
    public Map<String, String> getTableNameFromDDL(String ddlSql){
        Map<String, String> tableNameMap = new HashMap<>();
        String databaseName = null;
        String tableName = null;
        String[] ddlElements = ddlSql.split("\\s+");
        if (ddlElements.length>2 && ddlElements[0].equalsIgnoreCase("alter") && ddlElements[1].equalsIgnoreCase("table")){
            String tableEntireName = ddlElements[2];
            String[] tableNameElements = tableEntireName.split("\\.");
//            String tableSingleName = null;
            if (tableNameElements.length>1){
                databaseName = tableNameElements[0];
                tableName = tableNameElements[1];
                tableNameMap.put("databaseName", databaseName.replaceAll("`",""));
                tableNameMap.put("tableName", tableName.replaceAll("`",""));
            }else {
                tableName = tableNameElements[0];
                tableNameMap.put("databaseName", null);
                tableNameMap.put("tableName", tableName.replaceAll("`",""));
//                tableSingleName = tableEntireName;
            }
//            tableName = tableSingleName.replaceAll("`","");
        }
        return tableNameMap;
    }
    public void processQueryEvent(Event event) throws InterruptedException {
        logger.debug("Query事件，检查是不是Alter，一般是BEGIN");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());
        //检查sql是不是alter开头的，如果是，触发刷新TableMap条件
        QueryEventData queryEventData = (QueryEventData) event.getData();
        String eventDatabaseName = queryEventData.getDatabase();
        String sql = queryEventData.getSql();
        if (sql.startsWith("alter")){
            logger.debug("QUERY事件为alter语句");
            Map<String, String> tableNameMap = getTableNameFromDDL(sql);
            String databaseName = tableNameMap.get("databaseName");
            String tableName = tableNameMap.get("tableName");

            logger.debug("event database: " + eventDatabaseName +"; ddl database: " + databaseName + " tableName : "+ tableName );

            //这里，如果是在A数据库中，alter B数据库的table结构，那么databaseName是A，tableNameMapDatabaseName是B，不一样。
            //所以如果alter语句中有database名称，使用alter语句中的，如果没有，使用event中的。
            if (databaseName == null){
                databaseName = eventDatabaseName;
            }
            if (binlogFilterService.filterDDL(databaseName, tableName)){

                binlogTransaction.addDDLSql(databaseName + ":" + sql);
                binlogTransaction.setNextBinlogPositionEntity(this.binlogNextPositionEntity);
                logger.info("开始提交ALTER事务: " + sql);
//                commitTransactionOrDDL(binlogTransaction);
                if (!outputAndSavePosition(binlogTransaction)){
                    logger.error("发送事务ALTER SQL消息遇到异常");
                    throw new InterruptedException("发送事务ALTER SQL消息遇到异常");
                }
                logger.info("结束提交ALTER事务");
                schemaCacheService.reload();
            }
        }
    }
    public void processWriteRowsEvent(Event event) throws Exception {
        logger.debug("WRITE_ROWS事件，如果是操作的Table是Filter中的，那么准备在事务结束时保存Position");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());

        WriteRowsEventData dmlData = (WriteRowsEventData) event.getData();

//        BinlogTableMapEntity binlogTableMapEntity = binlogTableMapService.get(dmlData.getTableId());
        SchemaTableDef tableDef = schemaCacheService.getTableMetaData(dmlData.getTableId());

        if (tableDef != null && !tableDef.getTableName().isEmpty()){

            String databaseName = tableDef.getDatabaseName();
            String tableName = tableDef.getTableName();

            if ( binlogFilterService.filterDML(databaseName, tableName, "INSERT")){
//                logger.info("符合要求的WRITE_ROW事件Data:"+dmlData.toString());
                BitSet includedColumnsBitSet = dmlData.getIncludedColumns();

                List<SchemaColumnDef> validColumnDefList = tableDef.getSelectedColumns(includedColumnsBitSet);
                if (validColumnDefList.size() == 0){
                    throw new Exception("获取表结构定义失败: " + databaseName + "." + tableName);
                }
                List<String> validColumnNameList = new ArrayList<String>();

                validColumnDefList.forEach(new Consumer<SchemaColumnDef>() {
                    @Override
                    public void accept(SchemaColumnDef columnDef) {
                        validColumnNameList.add(columnDef.getColumnName());
                    }
                });

                List<Serializable[]> rows = dmlData.getRows();

                for (Serializable[] row: rows){
//                    addRow("WRITE", dmlData.getTableId(), row);
                    List<Object> columnValueList = binlogRowDataDeserializer.deserializeRowData(validColumnDefList, row);
//                    for (Object object:columnValueList) {
//                        logger.info("deserialize之后的columnValue: " + (object!=null?object.toString():"null"));
//                    }
                    if (validColumnDefList.size() == columnValueList.size()){

                        String builtSql = buildInsertSql(databaseName, tableName, validColumnNameList, columnValueList);
                        txStringBuffer.append(builtSql);
                        binlogTransaction.addRowRecord("insert", databaseName, tableName,validColumnNameList,columnValueList,null,null);

                    }else{
                        logger.error("有问题, columnDef count : " + validColumnDefList.size() + ", columnValue count: " + columnValueList.size());
                    }
                }

                logger.debug("符合要求的INSERT_ROWS事件处理完成，设置CommitFlag");
                this.binlogCommitFlag = 1;
                this.limitCheckAndProcess();
            }
        }else{
            logger.error("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId() + "，重新处理");
            throw new InterruptedException("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId());
        }

    }
    public void processUpdateRowsEvent(Event event) throws Exception {
        logger.debug("UPDATE_ROWS事件，如果是操作的Table是Filter中的，那么准备在事务结束时保存Position");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());

        UpdateRowsEventData dmlData = (UpdateRowsEventData) event.getData();
        SchemaTableDef tableDef = schemaCacheService.getTableMetaData(dmlData.getTableId());

        if (tableDef != null && !tableDef.getTableName().isEmpty()){
            String databaseName = tableDef.getDatabaseName();
            String tableName = tableDef.getTableName();
            if ( binlogFilterService.filterDML(databaseName, tableName, "UPDATE")){
                logger.debug("符合要求的UPDATE_ROW事件Data:"+dmlData.toString());
                BitSet includedColumnsBitSet = dmlData.getIncludedColumns();

                List<SchemaColumnDef> validColumnDefList = tableDef.getSelectedColumns(includedColumnsBitSet);
                List<String> validColumnNameList = new ArrayList<String>();

                validColumnDefList.forEach(new Consumer<SchemaColumnDef>() {
                    @Override
                    public void accept(SchemaColumnDef columnDef) {
                        validColumnNameList.add(columnDef.getColumnName());
                    }
                });

                BitSet whereColumnsBitSet = dmlData.getIncludedColumnsBeforeUpdate();
                List<SchemaColumnDef> whereColumnDefList = tableDef.getSelectedColumns(whereColumnsBitSet);
                List<String> whereColumnNameList = new ArrayList<String>();

                whereColumnDefList.forEach(new Consumer<SchemaColumnDef>() {
                    @Override
                    public void accept(SchemaColumnDef columnDef) {
                        whereColumnNameList.add(columnDef.getColumnName());
                    }
                });

                List<Map.Entry<Serializable[], Serializable[]>> rows = dmlData.getRows();

                for (Map.Entry<Serializable[], Serializable[]> row : rows) {
                    Serializable[] oldRow = row.getKey();
                    Serializable[] newRow = row.getValue();

                    List<Object> columnOldValueList = binlogRowDataDeserializer.deserializeRowData(whereColumnDefList, oldRow);

                    List<Object> columnNewValueList = binlogRowDataDeserializer.deserializeRowData(validColumnDefList, newRow);

                    if (validColumnDefList.size() == columnNewValueList.size() && whereColumnDefList.size() == columnOldValueList.size()){

                        String builtSql = buildUpdateSql(databaseName, tableName, validColumnNameList, columnNewValueList,whereColumnNameList,columnOldValueList);
                        txStringBuffer.append(builtSql);
                        binlogTransaction.addRowRecord("update", databaseName, tableName, validColumnNameList, columnNewValueList, whereColumnNameList, columnOldValueList);

                    }else{
                        logger.error("有问题, columnDef count : " + validColumnDefList.size() + ", columnNewValue count: " + columnNewValueList.size()
                        +", whereColumnDef count: " + whereColumnDefList.size() + ", columnOldValue count: " + columnOldValueList.size());
                    }

                }
                logger.debug("符合要求的UPDATE_ROWS事件处理完成，设置CommitFlag");
                this.binlogCommitFlag = 1;
                this.limitCheckAndProcess();
            }

        }else{
            logger.error("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId() + "，重新处理");
            throw new InterruptedException("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId());
        }


    }
    public void processDeleteRowsEvent(Event event) throws Exception {
        logger.debug("DELETE_ROWS事件，如果是操作的Table是Filter中的，那么准备在事务结束时保存Position");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());

        DeleteRowsEventData dmlData = (DeleteRowsEventData) event.getData();
        SchemaTableDef tableDef = schemaCacheService.getTableMetaData(dmlData.getTableId());

        if (tableDef != null && !tableDef.getTableName().isEmpty()){

            String databaseName = tableDef.getDatabaseName();
            String tableName = tableDef.getTableName();

            if ( binlogFilterService.filterDML(databaseName, tableName, "DELETE")){
                logger.debug("符合要求的DELETE_ROW事件Data:"+dmlData.toString());
                BitSet includedColumnsBitSet = dmlData.getIncludedColumns();

                List<SchemaColumnDef> validColumnDefList = tableDef.getSelectedColumns(includedColumnsBitSet);
                List<String> validColumnNameList = new ArrayList<String>();

                validColumnDefList.forEach(new Consumer<SchemaColumnDef>() {
                    @Override
                    public void accept(SchemaColumnDef columnDef) {
                        validColumnNameList.add(columnDef.getColumnName());
                    }
                });

                List<Serializable[]> rows = dmlData.getRows();

                for (Serializable[] row: rows){
//                    addRow("WRITE", dmlData.getTableId(), row);
                    List<Object> columnValueList = binlogRowDataDeserializer.deserializeRowData(validColumnDefList, row);
                    if (validColumnDefList.size() == columnValueList.size()){

                        String builtSql = buildDeleteSql(databaseName, tableName, validColumnNameList, columnValueList);
                        txStringBuffer.append(builtSql);
                        binlogTransaction.addRowRecord("delete", databaseName, tableName, validColumnNameList, columnValueList,null,null);

                    }else{
                        logger.error("有问题, columnDef count : " + validColumnDefList.size() + ", columnValue count: " + columnValueList.size());
                    }
                }

                logger.debug("符合要求的DELETE_ROWS事件处理完成，设置CommitFlag");
                this.binlogCommitFlag = 1;
                this.limitCheckAndProcess();
            }
        }else{
            logger.error("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId() + "，重新处理");
            throw new InterruptedException("TableMap中找不到对应的表信息。tableId:" + dmlData.getTableId());
        }

    }
    public void processXIDEvent(Event event) throws InterruptedException {
        logger.debug("XID事件，如果是操作的Table是Filter中的，那么此时要保存Position");
        //更新内存中的binlog position
        EventHeaderV4 eventHeader = event.getHeader();
        this.binlogNextPositionEntity.setServerId(eventHeader.getServerId());
        this.binlogNextPositionEntity.setBinlogPosition(eventHeader.getNextPosition());

        XidEventData xidEventData = event.getData();
        long xid = xidEventData.getXid();

        if (this.binlogCommitFlag == 1){
            logger.debug("关心的事务结束，保存position");
            binlogTransaction.setNextBinlogPositionEntity(this.binlogNextPositionEntity);
            binlogTransaction.setXid(xid);
            logger.debug("开始提交事务");
//            commitTransactionOrDDL(binlogTransaction);
            if (!outputAndSavePosition(binlogTransaction)){
                logger.error("发送事务SQL消息遇到异常");
                throw new InterruptedException("发送事务SQL消息遇到异常");
            }
            logger.debug("结束提交事务");
        }
    }

    public void limitCheckAndProcess() throws InterruptedException {
        if (this.binlogTransaction.getNumberOfRowRecords()>this.binlogCommitCountLimit-1){
            logger.debug("SQL数量到达Limit, 当前值: " + this.binlogTransaction.getNumberOfRowRecords());
            binlogTransaction.setNextBinlogPositionEntity(this.binlogNextPositionEntity);
            logger.debug("保存position，开始提交PART事务");
//            binlogTransaction.setXid(xid);
//            commitTransactionOrDDL(binlogTransaction);
            if (!outputAndSavePosition(binlogTransaction)){
                logger.error("发送PART事务SQL消息遇到异常");
                throw new InterruptedException("发送事务SQL消息遇到异常");
            }
            logger.debug("完成提交PART事务");
        }
    }

    private boolean outputAndSavePosition(BinlogTransaction binlogTransaction){
        boolean ret = false;

        try {
            String outStr = binlogTransaction.toJson();
            if (this.binlogOutput.output(outStr)){
                logger.info("成功发送事务SQL消息:" + binlogTransaction.toJson());
                binlogPositionService.saveNextPosition(this.binlogNextPositionEntity);

                if (outStr.length()>2048){
                    this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_LAST_OUTPUT_TRX,
                            "SQL太长,仅保存前1024字符,详细的请检查日志:" + outStr.substring(0,2047));
                }else {
                    this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_LAST_OUTPUT_TRX,
                            outStr);
                }


                this.binlogCommitFlag = 0;
                this.txStringBuffer = new StringBuffer();
                this.binlogTransaction = new BinlogTransaction();
                ret = true;
            }

        } catch (Exception e) {
            logger.error("发送事务SQL消息遇到异常: " + binlogTransaction.toString());
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "发送事务SQL消息遇到异常: " + binlogTransaction.toString() + "; 报错原因： " + e.getMessage());
        }
        return ret;
    }

    private void commitTransactionOrDDL(BinlogTransaction binlogTransaction){
        while (true){
            logger.info("事务Json.toString " + binlogTransaction.toString());
//              if (transactionSqlOutputQueue.offer(txStringBuffer.toString(), 1000, TimeUnit.MILLISECONDS)){
            try {
                if (transactionSqlOutputQueue.offer(binlogTransaction.toJson(), 1000, TimeUnit.MILLISECONDS)){
                    //保存Binlog Position
                    binlogPositionService.saveNextPosition(this.binlogNextPositionEntity);
                    this.binlogCommitFlag = 0;
                    txStringBuffer = new StringBuffer();
                    binlogTransaction = new BinlogTransaction();
                    return;
                }
            } catch (JsonProcessingException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private Object columnValueSQLWrapper(Object columnValue){
        if (columnValue instanceof String){
            return "'" + columnValue + "'";
        }else {
            return columnValue;
        }
    }

    private String buildDeleteSql(String schemaName, String tableName, List<String> whereColumnNames, List<Object> whereColumnValues){
        StringBuilder sb = new StringBuilder();
        if (whereColumnNames.size()>0){
            sb.append("delete from `").append(schemaName).append("`.`").append(tableName).append("`");

            sb.append(" where ");
            Object firstWhereColumnName = whereColumnNames.get(0);
            Object firstWhereColumnValue = whereColumnValues.get(0);

            sb.append("`").append(firstWhereColumnName).append("`");
            if (firstWhereColumnName==null){
                sb.append(" is ").append(columnValueSQLWrapper(firstWhereColumnValue));
            }else{
                sb.append("=").append(columnValueSQLWrapper(firstWhereColumnValue));
            }

            for (int i = 1; i< whereColumnNames.size(); i++){
                sb.append(" and `").append(whereColumnNames.get(i)).append("`");
                if (whereColumnValues.get(i)==null){
                    sb.append(" is ").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                }else{
                    sb.append("=").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                }
            }
            sb.append(" ;\n");
        }
        return sb.toString();
    }

    private String buildInsertSql(String schemaName, String tableName, List<String> columnNames, List<Object> columnValues){

        StringBuilder sb = new StringBuilder();

        if (columnNames.size()>0){
            sb.append("insert into `").append(schemaName).append("`.`").append(tableName).append("` ( ");

            Object firstColumnName = columnNames.get(0);
            sb.append("`").append(firstColumnName).append("`");

            for (int i = 1; i < columnNames.size(); i++){
                sb.append(",`").append(columnNames.get(i)).append("`");
            }

            sb.append(" ) values ( ");

            Object firstColumnValue = columnValues.get(0);
            sb.append(columnValueSQLWrapper(firstColumnValue));
//            if (firstColumnValue instanceof String){
//                sb.append("'").append(firstColumnValue).append("'");
//            }else {
//                sb.append(firstColumnValue);
//            }

            for (int i = 1; i< columnValues.size(); i++){
                sb.append(" , ").append(columnValueSQLWrapper(columnValues.get(i)));
//                Object columnValue = ;
//                if (columnValue instanceof String){
//                    sb.append(", '").append(columnValue).append("'");
//                }else {
//                    sb.append(", ").append(columnValue);
//                }
            }
            sb.append(" ) ;\n");
        }

        return sb.toString();
    }

    private String buildUpdateSql(String schemaName, String tableName, List<String> columnNames, List<Object> columnValues, List<String> whereColumnNames, List<Object> whereColumnValues){
        StringBuilder sb = new StringBuilder();

        if (columnValues.size()>0){
            sb.append("update `").append(schemaName).append("`.`").append(tableName).append("` set ");

            Object firstColumnName = columnNames.get(0);
            Object firstColumnValue = columnValues.get(0);

            sb.append("`").append(firstColumnName).append("`");
            sb.append("=").append(columnValueSQLWrapper(firstColumnValue));
//            if (firstColumnValue instanceof String){
//                sb.append("'").append(firstColumnValue).append("'");
//            }else {
//                sb.append(firstColumnValue);
//            }

            for (int i = 1; i < columnNames.size() ; i++){

                sb.append(", `").append(columnNames.get(i)).append("`");
                sb.append("=").append(columnValueSQLWrapper(columnValues.get(i)));
//                Object columnValue = columnValues.get(i);
//                if (columnValue instanceof String){
//                    sb.append("'").append(columnValues.get(i)).append("'").append(", `");
//                }else {
//                    sb.append(columnValues.get(i)).append(", `");
//                }

            }

            if (whereColumnNames.size()>0){

                sb.append(" where ");
                Object firstWhereColumnName = whereColumnNames.get(0);
                Object firstWhereColumnValue = whereColumnValues.get(0);

                sb.append("`").append(firstWhereColumnName).append("`");
                if (firstWhereColumnName==null){
                    sb.append(" is ").append(columnValueSQLWrapper(firstWhereColumnValue));
                }else{
                    sb.append("=").append(columnValueSQLWrapper(firstWhereColumnValue));
                }

                for (int i = 1; i< whereColumnNames.size(); i++){
                    sb.append(" and `").append(whereColumnNames.get(i)).append("`");
                    if (whereColumnValues.get(i)==null){
                        sb.append(" is ").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                    }else{
                        sb.append("=").append(columnValueSQLWrapper(whereColumnValues.get(i)));
                    }
                }
            }

            sb.append(";\n");
        }

        return sb.toString();
    }

}

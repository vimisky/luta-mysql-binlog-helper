package io.github.vimisky.luta.mysql.binlog.helper.replicator;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusPersistService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BinlogEventListenerImpl implements BinaryLogClient.EventListener {

    private static final Logger logger = LoggerFactory.getLogger(BinlogEventListenerImpl.class);

    private BlockingQueue<Event> binlogEventIncomingQueue;
    private Event lastEvent;
    private BinlogStatusService binlogStatusService;
    private boolean stopFlag = false;
    private Date lastEventSentTime = new Date();

    public BinlogEventListenerImpl(BlockingQueue<Event> binlogEventIncomingQueue) {
        this.binlogEventIncomingQueue = binlogEventIncomingQueue;
    }

    public Event getLastEvent() {
        return lastEvent;
    }

    public BinlogStatusService getBinlogStatusService() {
        return binlogStatusService;
    }

    public void setBinlogStatusService(BinlogStatusService binlogStatusService) {
        this.binlogStatusService = binlogStatusService;
    }

    public boolean isStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    @Override
    public void onEvent(Event event) {
//        logger.info("从数据库服务器获取到Binlog新事件");
//        logger.info("当前时间戳:"+System.currentTimeMillis());
//        logger.info("事件时间戳:"+event.getHeader().getTimestamp()+"事件类型:"+event.getHeader().getEventType());
        Date now = new Date();
        if(( now.getTime() -  this.lastEventSentTime.getTime()) > 60){
            binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_LAST_EVENT);
            this.lastEventSentTime = now;
        } ;

        try {
            //将事件对象传输到本地队列，供后续阻塞处理
            //之所以没有在onEvent事件处理回调方法中处理，是因为OnEvent是事件驱动的，没办法控制流程，遇到Exception后不能控制阻断后续Event。
            while (!stopFlag){
                if (binlogEventIncomingQueue.offer(event, 1000, TimeUnit.MILLISECONDS)){
//                  logger.info("将事件对象插入队列成功!");
                    this.lastEvent = event;
                    return;
                }
                Thread.sleep(10000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                    "将事件对象传输到本地队列遇到异常");
        }

    }
}

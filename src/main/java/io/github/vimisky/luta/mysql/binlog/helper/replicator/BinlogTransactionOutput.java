package io.github.vimisky.luta.mysql.binlog.helper.replicator;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogPositionService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BinlogTransactionOutput {

    public final static Logger logger = LoggerFactory.getLogger(BinlogTransactionOutput.class);

    private boolean stopFlag = false;
    private String currentSql = null;

    private BlockingQueue<String> transactionSqlOutputQueue;
    private RabbitTemplate outputRabbitTemplate;
    private BinlogStatusService binlogStatusService;
    private BinlogPositionService binlogPositionService;

    public BinlogTransactionOutput(BlockingQueue<String> transactionSqlOutputQueue, RabbitTemplate outputRabbitTemplate) {
        this.transactionSqlOutputQueue = transactionSqlOutputQueue;
        this.outputRabbitTemplate = outputRabbitTemplate;
    }

    public BinlogTransactionOutput(BlockingQueue<String> transactionSqlOutputQueue, RabbitTemplate outputRabbitTemplate, BinlogStatusService binlogStatusService, BinlogPositionService binlogPositionService) {
        this.transactionSqlOutputQueue = transactionSqlOutputQueue;
        this.outputRabbitTemplate = outputRabbitTemplate;
        this.binlogStatusService = binlogStatusService;
        this.binlogPositionService = binlogPositionService;
    }

    public boolean isStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public BinlogStatusService getBinlogStatusService() {
        return binlogStatusService;
    }

    public void setBinlogStatusService(BinlogStatusService binlogStatusService) {
        this.binlogStatusService = binlogStatusService;
    }

    public BinlogPositionService getBinlogPositionService() {
        return binlogPositionService;
    }

    public void setBinlogPositionService(BinlogPositionService binlogPositionService) {
        this.binlogPositionService = binlogPositionService;
    }

    public String getStatus(){
        String status = "stopFlag: " + this.stopFlag + ";";
        if (this.currentSql != null){
            status += "TransactionOutput currentSql : " + this.currentSql + ";";
        }
        return status;
    }

    public void start(){

        while (!this.stopFlag){

            String sql = null;
            try {
                if (this.currentSql == null){
                    sql = transactionSqlOutputQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if (sql == null){
                        logger.trace("输出事务SQL队列等待超时，重新等待");
                        continue;
                    }else{
                        this.currentSql = sql;
                        logger.info("从队列中取出新事务SQL消息:" + this.currentSql);
                    }
                }else{
                    logger.info("处理之前未发送成功的事务SQL消息:" + this.currentSql);
                    sql = this.currentSql;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                        "从事务SQL队列拉取信息遇到异常");
                continue;
            }

            try{
                logger.info("开始发送事务SQL到消息队列: " + sql);
                outputRabbitTemplate.convertAndSend(sql);
                logger.info("完成发送事务SQL到消息队列");
                this.currentSql = null;
            }catch(Exception e){
                e.printStackTrace();
                this.binlogStatusService.updateReplicatorStatus(BinlogStatusService.ReplicatorStatus.R_ERROR,
                        "发送事务SQL消息遇到异常");
            }

        }


    }

}

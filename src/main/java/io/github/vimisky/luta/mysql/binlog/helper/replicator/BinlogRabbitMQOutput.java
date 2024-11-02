package io.github.vimisky.luta.mysql.binlog.helper.replicator;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class BinlogRabbitMQOutput implements BinlogOutput{

    public final static Logger logger = LoggerFactory.getLogger(BinlogRabbitMQOutput.class);
    private RabbitTemplate outputRabbitTemplate;

    public BinlogRabbitMQOutput(RabbitTemplate outputRabbitTemplate) {
        this.outputRabbitTemplate = outputRabbitTemplate;
    }

//    @Override
//    public boolean output(String binlogTransactionJson) {
//        boolean ret = false;
//        try {
//            logger.debug("开始发送事务SQL到RabbitMQ: " + binlogTransactionJson);
//            outputRabbitTemplate.convertAndSend(binlogTransactionJson);
//            ret = true;
//            logger.debug("完成发送事务SQL到RabbitMQ");
//        }catch(Exception e){
//            e.printStackTrace();
//            ret = false;
//        }
//
//        return ret;
//    }
    @Override
    public boolean output(String binlogTransactionJson) throws Exception {
        boolean ret = false;
        logger.debug("开始发送事务SQL到RabbitMQ: " + binlogTransactionJson);
        outputRabbitTemplate.convertAndSend(binlogTransactionJson);
        ret = true;
        logger.debug("完成发送事务SQL到RabbitMQ");

        return ret;
    }


}

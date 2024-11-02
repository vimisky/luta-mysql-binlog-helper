package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogPositionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class BinlogPositionService {

    private static final Logger logger = LoggerFactory.getLogger(BinlogPositionService.class);

    private String taskUUID;
    private BinlogPositionPersistService binlogPositionPersistService;

    public BinlogPositionService(String taskUUID, BinlogPositionPersistService binlogPositionPersistService) {
        this.taskUUID = taskUUID;
        this.binlogPositionPersistService = binlogPositionPersistService;
    }

    public BinlogPositionPersistService getBinlogPositionPersistService() {
        return binlogPositionPersistService;
    }

    public void setBinlogPositionPersistService(BinlogPositionPersistService binlogPositionPersistService) {
        this.binlogPositionPersistService = binlogPositionPersistService;
    }

    public BinlogPositionEntity getNextPosition(){
        Map<String, Object> positionMap = binlogPositionPersistService.getNextPosition(this.taskUUID);
        BinlogPositionEntity binlogPositionEntity = new BinlogPositionEntity();
        binlogPositionEntity.setServerId((Long)positionMap.get("serverId"));
        binlogPositionEntity.setBinlogFilename((String)positionMap.get("filename"));
        binlogPositionEntity.setBinlogPosition((Long)positionMap.get("nextPosition"));
        return binlogPositionEntity;
    }

    public void saveNextPosition(BinlogPositionEntity binlogPositionEntity){
        if (binlogPositionEntity!=null){
            Map<String, Object> positionMap = new HashMap<>();
            positionMap.put("serverId", binlogPositionEntity.getServerId());
            positionMap.put("filename", binlogPositionEntity.getBinlogFilename());
            positionMap.put("nextPosition", binlogPositionEntity.getBinlogPosition());
            binlogPositionPersistService.saveNextPosition(taskUUID, positionMap);
        }
    }

}

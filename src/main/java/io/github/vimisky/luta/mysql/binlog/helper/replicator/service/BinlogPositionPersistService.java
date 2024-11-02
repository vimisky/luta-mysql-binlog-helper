package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogPositionEntity;

import java.util.Map;

public interface BinlogPositionPersistService {
    public Map<String, Object> getNextPosition(String taskUUID);
    public void saveNextPosition(String taskUUID, Map<String, Object> nextPosition);
}

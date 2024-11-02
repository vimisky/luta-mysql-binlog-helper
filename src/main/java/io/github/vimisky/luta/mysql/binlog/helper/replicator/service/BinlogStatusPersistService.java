package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

public interface BinlogStatusPersistService {
    public void updateTaskStopStatus(String taskUUID);
    public void updateTaskStartStatus(String taskUUID);
    public void updateTaskHeartbeatStatus(String taskUUID);
    public void updateTaskLastEventStatus(String taskUUID);
    public void updateTaskLastOutputTrxStatus(String taskUUID, String outputTrx);
    public void updateTaskErrorStatus(String taskUUID, String errorMessage);
}

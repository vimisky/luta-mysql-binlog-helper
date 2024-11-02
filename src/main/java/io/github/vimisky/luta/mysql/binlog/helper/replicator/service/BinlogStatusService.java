package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinlogStatusService {
    private static final Logger logger = LoggerFactory.getLogger(BinlogStatusService.class);

    public enum ReplicatorStatus {
        R_ERROR(-1, "错误"),
        R_STOP(0, "停止"),
        R_START(1, "运行"),
        R_HEARTBEAT(2, "心跳"),
        R_LAST_EVENT(3, "接收到Event"),
        R_LAST_OUTPUT_TRX(4, "已发送TRX");

        private Integer code;
        private String message;

        ReplicatorStatus(Integer code, String message){
            this.code = code;
            this.message = message;
        }

    }

    private String taskUUID;
    private BinlogStatusPersistService binlogStatusPersistService;

    public BinlogStatusService(String taskUUID, BinlogStatusPersistService binlogStatusPersistService) {
        this.taskUUID = taskUUID;
        this.binlogStatusPersistService = binlogStatusPersistService;
    }

    public void updateReplicatorStatus(ReplicatorStatus replicatorStatus){
        switch (replicatorStatus){
            case R_STOP:
                this.binlogStatusPersistService.updateTaskStopStatus(taskUUID);
                break;
            case R_START:
                this.binlogStatusPersistService.updateTaskStartStatus(taskUUID);
                break;
            case R_HEARTBEAT:
                this.binlogStatusPersistService.updateTaskHeartbeatStatus(taskUUID);
                break;
            case R_LAST_EVENT:
                this.binlogStatusPersistService.updateTaskLastEventStatus(taskUUID);
                break;
            case R_LAST_OUTPUT_TRX:
                this.binlogStatusPersistService.updateTaskLastOutputTrxStatus(taskUUID, "已发送TRX");
                break;
            case R_ERROR:
                this.binlogStatusPersistService.updateTaskErrorStatus(taskUUID, "请检查日志");
                break;
            default:
                break;
        }
    }
    public void updateReplicatorStatus(ReplicatorStatus replicatorStatus, String message){
        switch (replicatorStatus){
            case R_STOP:
                this.binlogStatusPersistService.updateTaskStopStatus(taskUUID);
                break;
            case R_START:
                this.binlogStatusPersistService.updateTaskStartStatus(taskUUID);
                break;
            case R_HEARTBEAT:
                this.binlogStatusPersistService.updateTaskHeartbeatStatus(taskUUID);
                break;
            case R_LAST_EVENT:
                this.binlogStatusPersistService.updateTaskLastEventStatus(taskUUID);
                break;
            case R_LAST_OUTPUT_TRX:
                this.binlogStatusPersistService.updateTaskLastOutputTrxStatus(taskUUID, message);
                break;
            case R_ERROR:
                this.binlogStatusPersistService.updateTaskErrorStatus(taskUUID, message);
                break;
            default:
                break;
        }
    }

}

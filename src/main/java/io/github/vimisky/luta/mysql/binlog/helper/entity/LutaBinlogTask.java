package io.github.vimisky.luta.mysql.binlog.helper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

public class LutaBinlogTask {
    private Long id;
    private String uuid;
    private Long binlogChannelId;
    private Date heartbeatTimestamp;
    private String executeNode;
    private Date executeStartTime;
    private Date executeStopTime;
    private Integer executeStatus;
    private String executeMessage;
    private Date lastEventTime;
    private Date lastOutputTrxTime;
    private String lastOutputTrx;
    private Long recServerId;
    private String recBinlogFilename;
    private Long recBinlogNextPosition;

    private LutaBinlogChannel lutaBinlogChannel;

    private Date createTime;
    private Date updateTime;

    public LutaBinlogTask() {
    }

    public LutaBinlogTask(LutaBinlogChannel lutaBinlogChannel) {
        this.setBinlogChannelId(lutaBinlogChannel.getId());
        this.setUuid(UUID.randomUUID().toString());
        this.setRecBinlogFilename(lutaBinlogChannel.getSrcBinlogFilename());
        this.setRecBinlogNextPosition(lutaBinlogChannel.getSrcBinlogNextPosition());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getBinlogChannelId() {
        return binlogChannelId;
    }

    public void setBinlogChannelId(Long binlogChannelId) {
        this.binlogChannelId = binlogChannelId;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getHeartbeatTimestamp() {
        return heartbeatTimestamp;
    }

    public void setHeartbeatTimestamp(Date heartbeatTimestamp) {
        this.heartbeatTimestamp = heartbeatTimestamp;
    }

    public String getExecuteNode() {
        return executeNode;
    }

    public void setExecuteNode(String executeNode) {
        this.executeNode = executeNode;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(Date executeStartTime) {
        this.executeStartTime = executeStartTime;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getExecuteStopTime() {
        return executeStopTime;
    }

    public void setExecuteStopTime(Date executeStopTime) {
        this.executeStopTime = executeStopTime;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(Date lastEventTime) {
        this.lastEventTime = lastEventTime;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getLastOutputTrxTime() {
        return lastOutputTrxTime;
    }

    public void setLastOutputTrxTime(Date lastOutputTrxTime) {
        this.lastOutputTrxTime = lastOutputTrxTime;
    }

    public String getLastOutputTrx() {
        return lastOutputTrx;
    }

    public void setLastOutputTrx(String lastOutputTrx) {
        this.lastOutputTrx = lastOutputTrx;
    }

    public Long getRecServerId() {
        return recServerId;
    }

    public void setRecServerId(Long recServerId) {
        this.recServerId = recServerId;
    }

    public String getRecBinlogFilename() {
        return recBinlogFilename;
    }

    public void setRecBinlogFilename(String recBinlogFilename) {
        this.recBinlogFilename = recBinlogFilename;
    }

    public Long getRecBinlogNextPosition() {
        return recBinlogNextPosition;
    }

    public void setRecBinlogNextPosition(Long recBinlogNextPosition) {
        this.recBinlogNextPosition = recBinlogNextPosition;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public LutaBinlogChannel getLutaBinlogChannel() {
        return lutaBinlogChannel;
    }

    public void setLutaBinlogChannel(LutaBinlogChannel lutaBinlogChannel) {
        this.lutaBinlogChannel = lutaBinlogChannel;
    }

}

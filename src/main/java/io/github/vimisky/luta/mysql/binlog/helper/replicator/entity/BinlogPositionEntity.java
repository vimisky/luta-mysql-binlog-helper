package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

public class BinlogPositionEntity {

    public static final Integer REPLICA_TYPE_FILE_POSITION=0;
    public static final Integer REPLICA_TYPE_GTID=1;

    private Integer replicaType = REPLICA_TYPE_FILE_POSITION;
    private Integer gtid;
    private Long serverId;
    private String binlogFilename;
    private Long binlogPosition;

    public BinlogPositionEntity() {
    }

    public Integer getReplicaType() {
        return replicaType;
    }

    public void setReplicaType(Integer replicaType) {
        this.replicaType = replicaType;
    }

    public Integer getGtid() {
        return gtid;
    }

    public void setGtid(Integer gtid) {
        this.gtid = gtid;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public void setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
    }

    public Long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(Long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }
}

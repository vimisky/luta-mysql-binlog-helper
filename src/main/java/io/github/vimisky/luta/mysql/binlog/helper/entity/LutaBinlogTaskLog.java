package io.github.vimisky.luta.mysql.binlog.helper.entity;

import java.util.Date;

public class LutaBinlogTaskLog {
    private Long id;
    private String uuid;
    private Long binlogChannelId;
    private String binlogChannelContent;
    private Date time;
    private Integer level;
    private String log;

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

    public String getBinlogChannelContent() {
        return binlogChannelContent;
    }

    public void setBinlogChannelContent(String binlogChannelContent) {
        this.binlogChannelContent = binlogChannelContent;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}

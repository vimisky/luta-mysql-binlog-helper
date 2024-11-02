package io.github.vimisky.luta.mysql.binlog.helper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class LutaBinlogChannel {
    private Long id;
    private String name;
    private String description;
    private String srcDriverClassName;
    private String srcHost;
    private Integer srcPort;
    private String srcUsername;
    private String srcPassword;
    private Integer srcBinlogType;
    private String srcBinlogFilename;
    private Long srcBinlogNextPosition;
    private String srcGtidNext;
    private String remoteMysqlTimezone;

    private Integer dstMessageQueueType;
    private String dstHost;
    private Integer dstPort;
    private String dstVhost;
    private String dstUsername;
    private String dstPassword;
    private String dstTopicName;
    private String dstRoutingKey;
    private String dstKey;

//    private LutaBinlogTask lutaBinlogTask;
    private List<LutaBinlogFilter> lutaBinlogFilterList;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSrcDriverClassName() {
        return srcDriverClassName;
    }

    public void setSrcDriverClassName(String srcDriverClassName) {
        this.srcDriverClassName = srcDriverClassName;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public String getSrcUsername() {
        return srcUsername;
    }

    public void setSrcUsername(String srcUsername) {
        this.srcUsername = srcUsername;
    }

    public String getSrcPassword() {
        return srcPassword;
    }

    public void setSrcPassword(String srcPassword) {
        this.srcPassword = srcPassword;
    }

    public Integer getSrcBinlogType() {
        return srcBinlogType;
    }

    public void setSrcBinlogType(Integer srcBinlogType) {
        this.srcBinlogType = srcBinlogType;
    }

    public String getSrcBinlogFilename() {
        return srcBinlogFilename;
    }

    public void setSrcBinlogFilename(String srcBinlogFilename) {
        this.srcBinlogFilename = srcBinlogFilename;
    }

    public Long getSrcBinlogNextPosition() {
        return srcBinlogNextPosition;
    }

    public void setSrcBinlogNextPosition(Long srcBinlogNextPosition) {
        this.srcBinlogNextPosition = srcBinlogNextPosition;
    }

    public String getSrcGtidNext() {
        return srcGtidNext;
    }

    public void setSrcGtidNext(String srcGtidNext) {
        this.srcGtidNext = srcGtidNext;
    }

    public String getRemoteMysqlTimezone() {
        return remoteMysqlTimezone;
    }

    public void setRemoteMysqlTimezone(String remoteMysqlTimezone) {
        this.remoteMysqlTimezone = remoteMysqlTimezone;
    }

    public Integer getDstMessageQueueType() {
        return dstMessageQueueType;
    }

    public void setDstMessageQueueType(Integer dstMessageQueueType) {
        this.dstMessageQueueType = dstMessageQueueType;
    }

    public String getDstHost() {
        return dstHost;
    }

    public void setDstHost(String dstHost) {
        this.dstHost = dstHost;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public String getDstVhost() {
        return dstVhost;
    }

    public void setDstVhost(String dstVhost) {
        this.dstVhost = dstVhost;
    }

    public String getDstUsername() {
        return dstUsername;
    }

    public void setDstUsername(String dstUsername) {
        this.dstUsername = dstUsername;
    }

    public String getDstPassword() {
        return dstPassword;
    }

    public void setDstPassword(String dstPassword) {
        this.dstPassword = dstPassword;
    }

    public String getDstTopicName() {
        return dstTopicName;
    }

    public void setDstTopicName(String dstTopicName) {
        this.dstTopicName = dstTopicName;
    }

    public String getDstRoutingKey() {
        return dstRoutingKey;
    }

    public void setDstRoutingKey(String dstRoutingKey) {
        this.dstRoutingKey = dstRoutingKey;
    }

    public String getDstKey() {
        return dstKey;
    }

    public void setDstKey(String dstKey) {
        this.dstKey = dstKey;
    }

//    public LutaBinlogTask getLutaBinlogTask() {
//        return lutaBinlogTask;
//    }
//
//    public void setLutaBinlogTask(LutaBinlogTask lutaBinlogTask) {
//        this.lutaBinlogTask = lutaBinlogTask;
//    }

    public List<LutaBinlogFilter> getLutaBinlogFilterList() {
        return lutaBinlogFilterList;
    }

    public void setLutaBinlogFilterList(List<LutaBinlogFilter> lutaBinlogFilterList) {
        this.lutaBinlogFilterList = lutaBinlogFilterList;
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

}

package io.github.vimisky.luta.mysql.binlog.helper.entity;

import java.util.Date;

public class LutaBinlogFilter {
    private Long id;
    private Long binlogChannelId;
    private String name;
    private String description;
    private boolean schemaNameSpecified;
    private String schemaName;
    private boolean tableNameSpecified;
    private String tableName;
    private boolean insertIncluded;
    private boolean updateIncluded;
    private boolean deleteIncluded;
    private boolean ddlUpdate;

//    private LutaBinlogChannel lutaBinlogChannel;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBinlogChannelId() {
        return binlogChannelId;
    }

    public void setBinlogChannelId(Long binlogChannelId) {
        this.binlogChannelId = binlogChannelId;
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

    public boolean isSchemaNameSpecified() {
        return schemaNameSpecified;
    }

    public void setSchemaNameSpecified(boolean schemaNameSpecified) {
        this.schemaNameSpecified = schemaNameSpecified;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public boolean isTableNameSpecified() {
        return tableNameSpecified;
    }

    public void setTableNameSpecified(boolean tableNameSpecified) {
        this.tableNameSpecified = tableNameSpecified;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isInsertIncluded() {
        return insertIncluded;
    }

    public void setInsertIncluded(boolean insertIncluded) {
        this.insertIncluded = insertIncluded;
    }

    public boolean isUpdateIncluded() {
        return updateIncluded;
    }

    public void setUpdateIncluded(boolean updateIncluded) {
        this.updateIncluded = updateIncluded;
    }

    public boolean isDeleteIncluded() {
        return deleteIncluded;
    }

    public void setDeleteIncluded(boolean deleteIncluded) {
        this.deleteIncluded = deleteIncluded;
    }

    public boolean isDdlUpdate() {
        return ddlUpdate;
    }

    public void setDdlUpdate(boolean ddlUpdate) {
        this.ddlUpdate = ddlUpdate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

//    public LutaBinlogChannel getLutaBinlogChannel() {
//        return lutaBinlogChannel;
//    }
//
//    public void setLutaBinlogChannel(LutaBinlogChannel lutaBinlogChannel) {
//        this.lutaBinlogChannel = lutaBinlogChannel;
//    }
}

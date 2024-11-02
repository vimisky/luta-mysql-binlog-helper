package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

public class BinlogEventFilterRule {
    private String schemaName;
    private String tableName;
    private String dmlType;
    private boolean ddlUpdate;

    public BinlogEventFilterRule() {
    }

    public BinlogEventFilterRule(String schemaName, String tableName, String dmlType) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.dmlType = dmlType;
    }

    public BinlogEventFilterRule(String schemaName, String tableName, String dmlType, boolean ddlUpdate) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.dmlType = dmlType;
        this.ddlUpdate = ddlUpdate;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDmlType() {
        return dmlType;
    }

    public void setDmlType(String dmlType) {
        this.dmlType = dmlType;
    }

    public boolean isDdlUpdate() {
        return ddlUpdate;
    }

    public void setDdlUpdate(boolean ddlUpdate) {
        this.ddlUpdate = ddlUpdate;
    }
}

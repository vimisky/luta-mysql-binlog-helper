package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

import java.util.List;

public class BinlogRowRecord {
    private String type;
    private String schemaName;
    private String tableName;
    private List<String> columnNameList;
    private List<Object> columnValueList;
    private List<String> oldColumnNameList;
    private List<Object> oldColumnValueList;

    public BinlogRowRecord() {
    }

    public BinlogRowRecord(String type, String schemaName, String tableName, List<String> columnNameList, List<Object> columnValueList, List<String> oldColumnNameList, List<Object> oldColumnValueList) {
        this.type = type;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnNameList = columnNameList;
        this.columnValueList = columnValueList;
        this.oldColumnNameList = oldColumnNameList;
        this.oldColumnValueList = oldColumnValueList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public List<Object> getColumnValueList() {
        return columnValueList;
    }

    public void setColumnValueList(List<Object> columnValueList) {
        this.columnValueList = columnValueList;
    }

    public List<String> getOldColumnNameList() {
        return oldColumnNameList;
    }

    public void setOldColumnNameList(List<String> oldColumnNameList) {
        this.oldColumnNameList = oldColumnNameList;
    }

    public List<Object> getOldColumnValueList() {
        return oldColumnValueList;
    }

    public void setOldColumnValueList(List<Object> oldColumnValueList) {
        this.oldColumnValueList = oldColumnValueList;
    }
}

package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class SchemaTableDef {
    private Long tableId;
    private String databaseName;
    private String tableName;
    private List<SchemaColumnDef> columnList = new LinkedList<SchemaColumnDef>();

    public boolean addColumn(SchemaColumnDef columnDef){
        return columnList.add(columnDef);
    }
    public List<SchemaColumnDef> getSelectedColumns(BitSet bitSet){
        List<SchemaColumnDef> selectedColumns = new ArrayList<>();
        int i = 0;
        for (SchemaColumnDef columnDef : columnList) {
            if (bitSet.get(i)){
                selectedColumns.add(columnDef);
            }
            i++;
        }
        return selectedColumns;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<SchemaColumnDef> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<SchemaColumnDef> columnList) {
        this.columnList = columnList;
    }
}

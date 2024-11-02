package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

public class SchemaColumnDef {
    private String columnName;
    //例如，varchar; int
    private String dataType;
    //例如，varchar(255); int(10) unsigned
    private String columnType;
    //例如，utf8
    private String characterSetName;

    private long ordinalPosition;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getCharacterSetName() {
        return characterSetName;
    }

    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public long getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(long ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }
//    public abstract String parseBinlogSerializableRowDataToSQL(Serializable object);
//    public abstract Object parseBinlogSerializableRowDataToObject(Serializable object);
}

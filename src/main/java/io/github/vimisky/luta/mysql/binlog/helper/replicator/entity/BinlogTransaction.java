package io.github.vimisky.luta.mysql.binlog.helper.replicator.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BinlogTransaction {
    private BinlogPositionEntity nextBinlogPositionEntity;
    private Long xid;
    private boolean isDML;
    private String ddlSql;
    private List<BinlogRowRecord> rowRecordList = new LinkedList<>();

    public BinlogPositionEntity getNextBinlogPositionEntity() {
        return nextBinlogPositionEntity;
    }

    public void setNextBinlogPositionEntity(BinlogPositionEntity nextBinlogPositionEntity) {
        this.nextBinlogPositionEntity = nextBinlogPositionEntity;
    }

    public Long getXid() {
        return xid;
    }

    public void setXid(Long xid) {
        this.xid = xid;
    }
    public boolean addRowRecord(String type, String schemeName, String tableName,
                                List<String> columnNameList, List<Object> columnValueList,
                                List<String> oldColumnNameList, List<Object> oldColumnValueList){
        this.isDML = true;
        BinlogRowRecord binlogRowRecord = new BinlogRowRecord(type, schemeName,tableName,columnNameList,columnValueList,oldColumnNameList,oldColumnValueList);
        this.rowRecordList.add(binlogRowRecord);
        return true;
    }

    public Integer getNumberOfRowRecords(){
        return this.rowRecordList.size();
    }

    public void addDDLSql(String ddlSql) {
        this.isDML = false;
        this.ddlSql = ddlSql;
    }

    //为什么单独写一个toJson呢，因为有些字段就不传输了。加减字段也方便，而且可以去掉嵌套。
    public String toJson() throws JsonProcessingException {

        Map<String, Object> map = new HashMap<>();
        map.put("serverId", nextBinlogPositionEntity.getServerId());
        map.put("binlogFilename", nextBinlogPositionEntity.getBinlogFilename());
        map.put("nextPosition", nextBinlogPositionEntity.getBinlogPosition());
        map.put("xid", xid);

        if (this.isDML){
            List<Map> rowRecordMapList = new LinkedList<>();

            for (BinlogRowRecord binlogRowRecord: this.rowRecordList ) {
                Map<String, Object> binlogRowRecordMap = new HashMap<>();
                binlogRowRecordMap.put("schemaName", binlogRowRecord.getSchemaName());
                binlogRowRecordMap.put("tableName", binlogRowRecord.getTableName());
                binlogRowRecordMap.put("type", binlogRowRecord.getType());
                binlogRowRecordMap.put("columnNameList", binlogRowRecord.getColumnNameList());
                binlogRowRecordMap.put("columnValueList", binlogRowRecord.getColumnValueList());
                binlogRowRecordMap.put("oldColumnNameList", binlogRowRecord.getOldColumnNameList());
                binlogRowRecordMap.put("oldColumnValueList", binlogRowRecord.getOldColumnValueList());
                rowRecordMapList.add(binlogRowRecordMap);
            }
            map.put("dml", true);
            map.put("rowRecordList", rowRecordMapList);
        }else {
            map.put("dml", false);
            map.put("ddlSql", ddlSql);
        }

        return new ObjectMapper().writeValueAsString(map);

    }
}

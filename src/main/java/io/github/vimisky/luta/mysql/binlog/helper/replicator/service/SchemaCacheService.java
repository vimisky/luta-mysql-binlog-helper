package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.SchemaColumnDef;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.SchemaTableDef;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SchemaCacheService {
    private static final Logger logger = LoggerFactory.getLogger(SchemaCacheService.class);

    //不需要构造函数中赋值了，在类定义这里赋值
    private LRUMap<Long, SchemaTableDef> tableCache = new LRUMap<Long, SchemaTableDef>();
    private Integer capacity = DEFAULT_CAPACITY;
    private static Integer DEFAULT_CAPACITY=4;

    private JdbcTemplate jdbcTemplate;

    public SchemaCacheService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //用ORDINAL_POSITION保证字段顺序
    public List<SchemaColumnDef> getColumnDefFromMySQL(String databaseName, String tableName){
        String sql = "select table_name,column_name,data_type,column_type,character_set_name,ordinal_position " +
                " from information_schema.columns " +
                "where table_schema=? and table_name=? order by ORDINAL_POSITION";
        RowMapper<SchemaColumnDef> rowMapper = new BeanPropertyRowMapper<SchemaColumnDef>(SchemaColumnDef.class);
        List<SchemaColumnDef> colList = jdbcTemplate.query(sql, rowMapper, databaseName, tableName);
        logger.debug("表结构定义为 "+colList.toString());
        return colList;
    }

    public Integer getCapacity(){
        return this.tableCache.maxSize();
    }

    public void reload(){
        this.tableCache.clear();
    }

    public void printAllEntry(){
        logger.info("获取当前缓存的Table信息");
        Iterator<Map.Entry<Long, SchemaTableDef>> it = this.tableCache.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Long, SchemaTableDef> entry = it.next();
            long key = entry.getKey();
            SchemaTableDef tableDef = entry.getValue();

            logger.info("tableId: " + key + " tableName: " + tableDef.getTableName()
                    + " databaseName: " + tableDef.getDatabaseName());
            List<SchemaColumnDef> colList = tableDef.getColumnList();
            for (SchemaColumnDef columnDef:colList) {
                logger.info("ordinal position : " + columnDef.getOrdinalPosition() +" , column name " + columnDef.getColumnName() + " , column type " + columnDef.getColumnType() +
                        " , data type " + columnDef.getDataType() + ", csname " + columnDef.getCharacterSetName());
            }
        }
    }

    public LRUMap<Long, SchemaTableDef> getAll(){
        return this.tableCache;
    }

    //column顺序和binlog里面的value的顺序，一致性怎么保证，居然这么获取是一致的。
    //columns表有一个字段是original_position，哈哈哈哈，用这个排序就行了，终于解决了一致性的问题
    public void syncTableMetaData(Long tableId, String databaseName, String tableName){
        //存在则直接返回
        if (this.tableCache.get(tableId) != null) {
            logger.debug("命中TableCache，tableId: " + tableId + ", tableName: " + tableName);
            return;
        }
        logger.debug("未命中TableCache，tableId: " + tableId + ", tableName: " + tableName);

        List<SchemaColumnDef> colList = this.getColumnDefFromMySQL(databaseName, tableName);
        if (colList != null && colList.size() > 0){
            logger.debug("MySQL中获取表结构定义成功");
        }
        SchemaTableDef tableDef = new SchemaTableDef();
        tableDef.setDatabaseName(databaseName);
        tableDef.setTableName(tableName);
        tableDef.setTableId(tableId);
        for(SchemaColumnDef columnDef: colList){
            tableDef.addColumn(columnDef);
        }
        this.tableCache.put(tableId, tableDef);
    }

    public SchemaTableDef getTableMetaData(Long tableId){
        return this.tableCache.get(tableId);
    }

}

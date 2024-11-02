package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class BinlogFilterService {
    private static final Logger logger = LoggerFactory.getLogger(BinlogFilterService.class);

    private String taskUUID;
    private BinlogFilterPersistService binlogFilterPersistService;
    private HashMap<String, Object> filterMap;

    public BinlogFilterService(String taskUUID, BinlogFilterPersistService binlogFilterPersistService) {
        this.taskUUID = taskUUID;
        this.binlogFilterPersistService = binlogFilterPersistService;
        this.filterMap = binlogFilterPersistService.getAllRules(taskUUID);
    }
    public boolean filterDML(String schema, String table, String dmlType){
        logger.debug("filterMap:"+ filterMap.toString());
        boolean ret = false;
        if (filterMap.containsKey("全部")){
            HashMap<String, Object> tableFilterMap = (HashMap<String, Object>) filterMap.get("全部");
            if (tableFilterMap.containsKey("全部")){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get("全部");
                if (dmlddlFilterMap.containsKey(dmlType)){
                    ret = true;
                }

            }

            if (tableFilterMap.containsKey(table)){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get(table);
                if (dmlddlFilterMap.containsKey(dmlType)){
                    ret = true;
                }
            }
        }

        if (filterMap.containsKey(schema)){
            HashMap<String, Object> tableFilterMap = (HashMap<String, Object>) filterMap.get(schema);
            if (tableFilterMap.containsKey("全部")){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get("全部");
                if (dmlddlFilterMap.containsKey(dmlType)){
                    ret = true;
                }

            }

            if (tableFilterMap.containsKey(table)){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get(table);
                if (dmlddlFilterMap.containsKey(dmlType)){
                    ret = true;
                }
            }
        }
        return ret;
    }
    public boolean filterDDL(String schema, String table){
        boolean ret = false;
        if (filterMap.containsKey("全部")){
            HashMap<String, Object> tableFilterMap = (HashMap<String, Object>) filterMap.get("全部");
            if (tableFilterMap.containsKey("全部")){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get("全部");
                if (dmlddlFilterMap.containsKey("ddlUpdate")){
                    ret = true;
                }

            }

            if (tableFilterMap.containsKey(table)){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get(table);
                if (dmlddlFilterMap.containsKey("ddlUpdate")){
                    ret = true;
                }
            }
        }

        if (filterMap.containsKey(schema)){
            HashMap<String, Object> tableFilterMap = (HashMap<String, Object>) filterMap.get(schema);
            if (tableFilterMap.containsKey("全部")){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get("全部");
                if (dmlddlFilterMap.containsKey("ddlUpdate")){
                    ret = true;
                }

            }

            if (tableFilterMap.containsKey(table)){
                HashMap<String, Object> dmlddlFilterMap = (HashMap<String, Object>) tableFilterMap.get(table);
                if (dmlddlFilterMap.containsKey("ddlUpdate")){
                    ret = true;
                }
            }
        }
        return ret;
    }
}

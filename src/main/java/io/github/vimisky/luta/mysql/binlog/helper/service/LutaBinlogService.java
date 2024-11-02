package io.github.vimisky.luta.mysql.binlog.helper.service;

import io.github.vimisky.luta.mysql.binlog.helper.dao.*;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogChannel;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogFilter;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTaskLog;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.entity.BinlogPositionEntity;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogFilterPersistService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogFilterService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogPositionPersistService;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.service.BinlogStatusPersistService;
import io.github.vimisky.luta.mysql.binlog.helper.utils.LutaBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LutaBinlogService implements BinlogPositionPersistService, BinlogStatusPersistService, BinlogFilterPersistService {

    private static final Logger logger = LoggerFactory.getLogger(LutaBinlogService.class);

    @Autowired
    private LutaBinlogChannelMapper lutaBinlogChannelMapper;
    @Autowired
    private LutaBinlogFilterMapper lutaBinlogFilterMapper;
    @Autowired
    private LutaBinlogTaskMapper lutaBinlogTaskMapper;
    @Autowired
    private LutaBinlogTaskLogMapper lutaBinlogTaskLogMapper;
    @Autowired
    private LutaParameterConfigMapper lutaParameterConfigMapper;

    @Transactional
    public void fillTaskConfig(LutaBinlogTask binlogTask){
        Long binlogChannelId = binlogTask.getBinlogChannelId();
        List<LutaBinlogChannel> binlogChannelList = lutaBinlogChannelMapper.findById(binlogChannelId);
        if (binlogChannelList.size()>0) {
            LutaBinlogChannel binlogChannel = binlogChannelList.get(0);
            binlogTask.setLutaBinlogChannel(binlogChannel);
//            binlogChannel.setLutaBinlogTask(binlogTask);
            List<LutaBinlogFilter> binlogFilterList = lutaBinlogFilterMapper.findByBinlogChannelId(binlogChannelId);
            if (binlogFilterList.size()>0){
                binlogChannel.setLutaBinlogFilterList(binlogFilterList);
//                for (LutaBinlogFilter binlogFilter: binlogFilterList){
//                    binlogFilter.setLutaBinlogChannel(binlogChannel);
//                }
            }
        }
    }

//    @Transactional
//    public LutaBinlogTask getBinlogTask(Long id){
//        LutaBinlogTask binlogTask = null;
//        List<LutaBinlogTask> binlogTaskList = lutaBinlogTaskMapper.findById(id);
//        if (binlogTaskList.size()>0){
//            binlogTask = binlogTaskList.get(0);
//            this.fillTaskConfig(binlogTask);
//        }
//
//        return binlogTask;
//    }
    @Transactional
    public LutaBinlogTask getBinlogTask(String uuid){
        LutaBinlogTask binlogTask = null;
        List<LutaBinlogTask> binlogTaskList = lutaBinlogTaskMapper.findByUuid(uuid);
        if (binlogTaskList.size()>0){
            binlogTask = binlogTaskList.get(0);
            this.fillTaskConfig(binlogTask);
        }

        return binlogTask;
    }
    @Transactional
    public LutaBinlogTask getBriefBinlogTask(String uuid){
        LutaBinlogTask binlogTask = null;
        List<LutaBinlogTask> binlogTaskList = lutaBinlogTaskMapper.findByUuid(uuid);
        if (binlogTaskList.size()>0){
            binlogTask = binlogTaskList.get(0);
        }

        return binlogTask;
    }
    @Transactional
    public LutaBinlogTask createBinlogTask(LutaBinlogChannel lutaBinlogChannel){
        this.createBinlogChannel(lutaBinlogChannel);
        LutaBinlogTask lutaBinlogTask = new LutaBinlogTask(lutaBinlogChannel);
        lutaBinlogTaskMapper.insert(lutaBinlogTask);
        return  lutaBinlogTask;
    }

    @Transactional
    public List<LutaBinlogTask> getBinlogTaskList(){
        List<LutaBinlogTask> binlogTaskList = lutaBinlogTaskMapper.findAll();

        for (LutaBinlogTask binlogTask: binlogTaskList) {
            this.fillTaskConfig(binlogTask);
        }

        return binlogTaskList;
    }

    @Transactional
    public void deleteBinlogTask(String uuid){
        List<LutaBinlogTask> lutaBinlogTaskList = lutaBinlogTaskMapper.findByUuid(uuid);
        if (lutaBinlogTaskList.size()>0){
            LutaBinlogTask binlogTask = lutaBinlogTaskList.get(0);
            LutaBinlogChannel pBinlogChannel = binlogTask.getLutaBinlogChannel();
            if (pBinlogChannel!=null)
                this.deleteBinlogChannel(pBinlogChannel.getId());
            lutaBinlogTaskMapper.delete(binlogTask.getId());
        }
    }

    @Transactional
    public void updateBinlogTask(LutaBinlogTask lutaBinlogTask){
        LutaBinlogChannel lutaBinlogChannel = lutaBinlogTask.getLutaBinlogChannel();
        if (lutaBinlogChannel != null ){
            if (lutaBinlogChannel.getId()!=null){
                this.updateBinlogChannel(lutaBinlogChannel);
            }else{
                this.createBinlogChannel(lutaBinlogChannel);
                lutaBinlogTask.setBinlogChannelId(lutaBinlogChannel.getId());
            }
        }
        lutaBinlogTaskMapper.update(lutaBinlogTask);
    }

    @Transactional
    public void updateBinlogTaskConfig(String taskUUID, LutaBinlogChannel lutaBinlogChannel){
        LutaBinlogTask lutaBinlogTask = this.getBinlogTask(taskUUID);
        LutaBinlogChannel oldLutaBinlogChannel = lutaBinlogTask.getLutaBinlogChannel();
        if (lutaBinlogChannel !=null ){
            LutaBeanUtils.copyPropertiesIgnoreNull(lutaBinlogChannel, oldLutaBinlogChannel);
            lutaBinlogChannelMapper.update(oldLutaBinlogChannel);
        }
    }

    @Transactional
    public void updateBinlogTaskPosition(String taskUUID, String binlogFilename, Long nextPosition){
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        lutaBinlogTask.setRecBinlogFilename(binlogFilename);
        lutaBinlogTask.setRecBinlogNextPosition(nextPosition);
        lutaBinlogTaskMapper.update(lutaBinlogTask);
    }

    @Transactional
    public void createBinlogChannel(LutaBinlogChannel lutaBinlogChannel){
        lutaBinlogChannelMapper.insert(lutaBinlogChannel);
        List<LutaBinlogFilter> lutaBinlogFilterList = lutaBinlogChannel.getLutaBinlogFilterList();
        if (lutaBinlogFilterList != null){
            for ( LutaBinlogFilter lutaBinlogFilter: lutaBinlogFilterList ) {
                lutaBinlogFilter.setBinlogChannelId(lutaBinlogChannel.getId());
                lutaBinlogFilterMapper.insert(lutaBinlogFilter);
            }
        }
    }
    @Transactional
    public void updateBinlogChannel(LutaBinlogChannel lutaBinlogChannel){
        List<LutaBinlogFilter> lutaBinlogFilterList = lutaBinlogChannel.getLutaBinlogFilterList();
        for ( LutaBinlogFilter lutaBinlogFilter: lutaBinlogFilterList ) {
            lutaBinlogFilter.setBinlogChannelId(lutaBinlogChannel.getId());
            if (null != lutaBinlogFilter.getId()){
                lutaBinlogFilterMapper.update(lutaBinlogFilter);
            }else {
                lutaBinlogFilterMapper.insert(lutaBinlogFilter);
            }
        }
        lutaBinlogChannelMapper.update(lutaBinlogChannel);
    }
    @Transactional
    public void deleteBinlogChannel(Long id){
        List<LutaBinlogFilter> lutaBinlogFilterList = lutaBinlogFilterMapper.findByBinlogChannelId(id);
        for ( LutaBinlogFilter lutaBinlogFilter: lutaBinlogFilterList ) {
            lutaBinlogFilterMapper.delete(lutaBinlogFilter.getId());
        }
        lutaBinlogChannelMapper.delete(id);
    }


    @Override
    public Map<String, Object> getNextPosition(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        Map<String, Object> positionMap = null;
        if (lutaBinlogTask != null){
            positionMap = new HashMap<>();
            positionMap.put("serverId", lutaBinlogTask.getRecServerId());
            positionMap.put("filename", lutaBinlogTask.getRecBinlogFilename());
            positionMap.put("nextPosition", lutaBinlogTask.getRecBinlogNextPosition());
        }
        return positionMap;
    }

    @Override
    public void saveNextPosition(String taskUUID, Map<String, Object> positionMap) {
        logger.debug("saveNextPosition");
        Long serverId = (Long) positionMap.get("serverId");
        String filename = (String) positionMap.get("filename");
        Long nextPosition = (Long) positionMap.get("nextPosition");
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        logger.debug("saveNextPosition:" + " serverId: " + serverId + " filename:" + filename + " nextPosition:" + nextPosition);
        if (lutaBinlogTask != null){
            logger.debug("lutaBinlogTaskMapper.update start.");
            lutaBinlogTask.setRecServerId(serverId);
            lutaBinlogTask.setRecBinlogFilename(filename);
            lutaBinlogTask.setRecBinlogNextPosition(nextPosition);

            lutaBinlogTaskMapper.update(lutaBinlogTask);
            logger.debug("lutaBinlogTaskMapper.update ok.");
        }
    }

    @Override
    public void updateTaskStopStatus(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(0);
            lutaBinlogTask.setExecuteStopTime(new Date());
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }
    @Override
    public void updateTaskStartStatus(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(1);
            lutaBinlogTask.setExecuteStartTime(new Date());
            lutaBinlogTask.setExecuteMessage("");
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }
    @Override
    public void updateTaskHeartbeatStatus(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(2);
            lutaBinlogTask.setHeartbeatTimestamp(new Date());
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }
    @Override
    public void updateTaskLastEventStatus(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(3);
            lutaBinlogTask.setLastEventTime(new Date());
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }

    @Override
    public void updateTaskLastOutputTrxStatus(String taskUUID, String outputTrx) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(4);
            lutaBinlogTask.setLastOutputTrxTime(new Date());
            lutaBinlogTask.setLastOutputTrx(outputTrx);
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }

    @Override
    public void updateTaskErrorStatus(String taskUUID, String errorMessage) {
        LutaBinlogTask lutaBinlogTask = this.getBriefBinlogTask(taskUUID);
        if (lutaBinlogTask != null){
            lutaBinlogTask.setExecuteStatus(-1);
            lutaBinlogTask.setExecuteMessage(errorMessage);
            this.lutaBinlogTaskMapper.update(lutaBinlogTask);
        }
    }
    @Transactional
    public List<LutaBinlogFilter> getBinlogFilterAll(){
        List<LutaBinlogFilter> binlogFilterList = lutaBinlogFilterMapper.findAll();
        return binlogFilterList;
    }
    @Transactional
    public List<LutaBinlogFilter> getBinlogFilterList(Long binlogChannelId){
        List<LutaBinlogFilter> binlogFilterList = lutaBinlogFilterMapper.findByBinlogChannelId(binlogChannelId);
        return binlogFilterList;
    }
    @Transactional
    public LutaBinlogFilter getBinlogFilter(Long id){
        LutaBinlogFilter binlogFilter = null;
        List<LutaBinlogFilter> binlogFilterList = lutaBinlogFilterMapper.findById(id);
        if (binlogFilterList.size()>0){
            binlogFilter = binlogFilterList.get(0);
        }

        return binlogFilter;
    }
    @Transactional
    public void deleteBinlogFilter(Long id){
        lutaBinlogFilterMapper.delete(id);
    }
    @Transactional
    public void createBinlogFilter(LutaBinlogFilter lutaBinlogFilter){
        lutaBinlogFilterMapper.insert(lutaBinlogFilter);
    }
    @Transactional
    public void updateBinlogFilter(LutaBinlogFilter lutaBinlogFilter){
        lutaBinlogFilterMapper.update(lutaBinlogFilter);
    }

    @Override
    public HashMap<String, Object> getAllRules(String taskUUID) {
        LutaBinlogTask lutaBinlogTask = this.getBinlogTask(taskUUID);
        List<LutaBinlogFilter> lutaBinlogFilterList = lutaBinlogTask.getLutaBinlogChannel().getLutaBinlogFilterList();
        HashMap<String, Object> schemaFilterMap = new HashMap<String, Object>();
        for (LutaBinlogFilter lutaBinlogFilter:lutaBinlogFilterList) {
            boolean schemaNameSpecified = lutaBinlogFilter.isSchemaNameSpecified();
            String schemaName = lutaBinlogFilter.getSchemaName();
            boolean tableNameSpecified = lutaBinlogFilter.isTableNameSpecified();
            String tableName = lutaBinlogFilter.getTableName();
            boolean insertIncluded = lutaBinlogFilter.isInsertIncluded();
            boolean updateIncluded = lutaBinlogFilter.isUpdateIncluded();
            boolean deleteIncluded = lutaBinlogFilter.isDeleteIncluded();
            boolean ddlUpdate = lutaBinlogFilter.isDdlUpdate();
            HashMap<String, Object> dmlddlFilterMap = new HashMap<>();
            if (insertIncluded)  dmlddlFilterMap.put("INSERT",1);
            if (updateIncluded) dmlddlFilterMap.put("UPDATE",1);
            if (deleteIncluded) dmlddlFilterMap.put("DELETE",1);
            if (ddlUpdate) dmlddlFilterMap.put("ddlUpdate",1);
            if (!schemaNameSpecified){
                HashMap<String, Object> tableFilterMap = new HashMap<>();
                tableFilterMap.put("全部", dmlddlFilterMap);
                schemaFilterMap.put("全部", tableFilterMap);
            }else {
                if (!tableNameSpecified){
                    HashMap<String, Object> tableFilterMap = new HashMap<>();
                    tableFilterMap.put("全部", dmlddlFilterMap);
                    schemaFilterMap.put(schemaName, tableFilterMap);
                }else {
                    String[] specifiedTableNames = tableName.split("\\s+");
                    HashMap<String, Object> tableFilterMap = new HashMap<>();
                    for (String specifiedTableName:specifiedTableNames) {
                        tableFilterMap.put(specifiedTableName, dmlddlFilterMap);
                    }
                    schemaFilterMap.put(schemaName, tableFilterMap);
                }
            }
        }

        return schemaFilterMap;
    }
}

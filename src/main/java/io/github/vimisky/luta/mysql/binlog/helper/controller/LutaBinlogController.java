package io.github.vimisky.luta.mysql.binlog.helper.controller;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogChannel;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogFilter;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.BinlogReplicator;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.DebeziumReplicator;
import io.github.vimisky.luta.mysql.binlog.helper.service.LutaBinlogService;
import io.github.vimisky.luta.mysql.binlog.helper.service.LutaBinlogTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LutaBinlogController {
    private static final Logger logger = LoggerFactory.getLogger(LutaBinlogController.class);

    @Autowired
    private LutaBinlogService lutaBinlogService;
    @Autowired
    LutaBinlogTaskManager lutaBinlogTaskManager;

    @RequestMapping("/task/detail")
    public LutaApiResult taskDetail(String taskUUID){
        return LutaApiResultBuilder.ok(lutaBinlogService.getBinlogTask(taskUUID));
    }
    @RequestMapping("/task/list")
    public LutaApiResult<List<LutaBinlogTask>> taskList(){
        return LutaApiResultBuilder.ok(lutaBinlogService.getBinlogTaskList());
    }

    @RequestMapping("/task/remove")
    public LutaApiResult taskRemove(String taskUUID){
        LutaBinlogTask lutaBinlogTask = lutaBinlogService.getBinlogTask(taskUUID);
        if (lutaBinlogTaskManager.isRunning(lutaBinlogTask)){
            lutaBinlogTaskManager.stop(lutaBinlogTask);
        }
        lutaBinlogService.deleteBinlogTask(taskUUID);
        return LutaApiResultBuilder.ok();
    }
    @RequestMapping("/task/start")
    public LutaApiResult taskStart(String taskUUID){
        LutaBinlogTask lutaBinlogTask = lutaBinlogService.getBinlogTask(taskUUID);
        boolean ret = lutaBinlogTaskManager.start(lutaBinlogTask);
        if (ret){
            return LutaApiResultBuilder.ok();
        }else{
            lutaBinlogTask = lutaBinlogService.getBinlogTask(taskUUID);
            return LutaApiResultBuilder.error(lutaBinlogTask.getExecuteMessage());
        }

    }
    @RequestMapping("/task/stop")
    public LutaApiResult taskStop(String taskUUID){
        LutaBinlogTask lutaBinlogTask = lutaBinlogService.getBinlogTask(taskUUID);
        lutaBinlogTaskManager.stop(lutaBinlogTask);

        return LutaApiResultBuilder.ok();
    }
    @RequestMapping("/filter/detail")
    public LutaApiResult filterDetail(Long id){
        return LutaApiResultBuilder.ok(lutaBinlogService.getBinlogFilter(id));
    }
    @RequestMapping("/filter/list")
    public LutaApiResult<List<LutaBinlogFilter>> filterList(Long binlogChannelId){
        return LutaApiResultBuilder.ok(lutaBinlogService.getBinlogFilterList(binlogChannelId));
    }

    @RequestMapping("/filter/remove")
    public LutaApiResult filterRemove(Long id){
        lutaBinlogService.deleteBinlogFilter(id);
        return LutaApiResultBuilder.ok();
    }

    @RequestMapping("/dedezium/start")
    public LutaApiResult LutaDedeziumStart(){
        try {
            new DebeziumReplicator().startDebeziumEngine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LutaApiResultBuilder.ok();
    }


}

package io.github.vimisky.luta.mysql.binlog.helper.service;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import io.github.vimisky.luta.mysql.binlog.helper.replicator.BinlogReplicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class LutaBinlogTaskManager {

    @Autowired
    public LutaBinlogService lutaBinlogService;

    Map<String, BinlogReplicator> binlogReplicatorMap = new HashMap<>();

    @PreDestroy
    public void stopAllTask(){
        Collection<BinlogReplicator> binlogReplicatorCollection = binlogReplicatorMap.values();
        if (binlogReplicatorCollection!=null && binlogReplicatorCollection.size()>0){
            for ( BinlogReplicator binlogReplicator: binlogReplicatorCollection ) {
                binlogReplicator.stop();
            }
        }
    }

    public boolean stop(LutaBinlogTask lutaBinlogTask){

        BinlogReplicator binlogReplicator = binlogReplicatorMap.get(lutaBinlogTask.getUuid());
        if (binlogReplicator != null ){
            binlogReplicator.stop();
            binlogReplicatorMap.remove(lutaBinlogTask.getUuid());
        }

        return true;
    }

    public boolean start(LutaBinlogTask lutaBinlogTask){
        this.stop(lutaBinlogTask);
        BinlogReplicator binlogReplicator = new BinlogReplicator(lutaBinlogTask.getUuid(), lutaBinlogService);
        this.binlogReplicatorMap.put(lutaBinlogTask.getUuid(), binlogReplicator);

        return binlogReplicator.start();
    }

    public boolean isRunning(LutaBinlogTask lutaBinlogTask){
        boolean isRunning = false;
        BinlogReplicator binlogReplicator = binlogReplicatorMap.get(lutaBinlogTask.getUuid());
        if (binlogReplicator != null ){
            isRunning = true;
        }
        return isRunning;
    }


    public Integer getStatus(LutaBinlogTask binlogTask){
        return 0;
    }
}

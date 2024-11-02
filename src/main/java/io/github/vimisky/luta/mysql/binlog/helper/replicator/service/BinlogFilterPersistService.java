package io.github.vimisky.luta.mysql.binlog.helper.replicator.service;

import java.util.HashMap;

public interface BinlogFilterPersistService {
    public HashMap<String, Object> getAllRules(String taskUUID);
}

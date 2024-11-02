package io.github.vimisky.luta.mysql.binlog.helper.dao;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaBinlogTaskMapper {
    public List<LutaBinlogTask> findById(Long id);
    public List<LutaBinlogTask> findByUuid(String uuid);
    public List<LutaBinlogTask> findByBinlogChannelId(Long binlogChannelId);
    public List<LutaBinlogTask> findAll();
    public int insert(LutaBinlogTask lutaBinlogTask);
    public int update(LutaBinlogTask lutaBinlogTask);
    public int delete(Long id);
}

package io.github.vimisky.luta.mysql.binlog.helper.dao;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTaskLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaBinlogTaskLogMapper {
    public List<LutaBinlogTaskLog> findById(Long id);
    public List<LutaBinlogTaskLog> findByBinlogChannelId(Long binlogChannelId);
    public List<LutaBinlogTaskLog> findAll();
    public int insert(LutaBinlogTaskLog lutaBinlogTaskLog);
    public int update( LutaBinlogTaskLog lutaBinlogTaskLog);
    public int delete(Long id);
}

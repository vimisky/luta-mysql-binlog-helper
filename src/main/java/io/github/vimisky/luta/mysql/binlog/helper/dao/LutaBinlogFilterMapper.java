package io.github.vimisky.luta.mysql.binlog.helper.dao;


import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaBinlogFilterMapper {
    public List<LutaBinlogFilter> findById(Long id);
    public List<LutaBinlogFilter> findByBinlogChannelId(Long binlogChannelId);
    public List<LutaBinlogFilter> findAll();
    public int insert(LutaBinlogFilter lutaBinlogChannel);
    public int update(LutaBinlogFilter lutaBinlogChannel);
    public int delete(Long id);
}

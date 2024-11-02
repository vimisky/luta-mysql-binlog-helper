package io.github.vimisky.luta.mysql.binlog.helper.dao;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogChannel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaBinlogChannelMapper {
    public List<LutaBinlogChannel> findById(Long id);
    public List<LutaBinlogChannel> findAll();
    public int insert(LutaBinlogChannel lutaBinlogChannel);
    public int update(LutaBinlogChannel lutaBinlogChannel);
    public int delete(Long id);
}

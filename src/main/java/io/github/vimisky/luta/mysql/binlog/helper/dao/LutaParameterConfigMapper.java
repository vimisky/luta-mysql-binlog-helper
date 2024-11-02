package io.github.vimisky.luta.mysql.binlog.helper.dao;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaParameterConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaParameterConfigMapper {
    public List<LutaParameterConfig> findById(Long id);
    public List<LutaParameterConfig> findAll();
    public int insert(LutaParameterConfig lutaBinlogChannel);
    public int update(LutaParameterConfig lutaBinlogChannel);
    public int delete(Long id);
}

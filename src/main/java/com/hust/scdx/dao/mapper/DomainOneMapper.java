package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainOneExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DomainOneMapper {
    long countByExample(DomainOneExample example);

    int deleteByExample(DomainOneExample example);

    int deleteByPrimaryKey(String uuid);

    int insert(DomainOne record);
    
    int insertBatch(List<DomainOne> list);
    
    int insertIgnore(List<DomainOne> list);

    int insertSelective(DomainOne record);

    List<DomainOne> selectByExample(DomainOneExample example);

    DomainOne selectByPrimaryKey(String uuid);

    int updateByExampleSelective(@Param("record") DomainOne record, @Param("example") DomainOneExample example);

    int updateByExample(@Param("record") DomainOne record, @Param("example") DomainOneExample example);

    int updateByPrimaryKeySelective(DomainOne record);

    int updateByPrimaryKey(DomainOne record);
}
package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.DomainStore;
import com.hust.scdx.model.DomainStoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DomainStoreMapper {
    long countByExample(DomainStoreExample example);

    int deleteByExample(DomainStoreExample example);

    int insert(DomainStore record);

    int insertSelective(DomainStore record);
    
    int insertBatch(List<DomainStore> list);

    List<DomainStore> selectByExample(DomainStoreExample example);

    int updateByExampleSelective(@Param("record") DomainStore record, @Param("example") DomainStoreExample example);

    int updateByExample(@Param("record") DomainStore record, @Param("example") DomainStoreExample example);
}
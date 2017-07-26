package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.ExtfileExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExtfileMapper {
    long countByExample(ExtfileExample example);

    int deleteByExample(ExtfileExample example);

    int deleteByPrimaryKey(String extfileId);

    int insert(Extfile record);

    int insertSelective(Extfile record);

    List<Extfile> selectByExample(ExtfileExample example);

    Extfile selectByPrimaryKey(String extfileId);

    int updateByExampleSelective(@Param("record") Extfile record, @Param("example") ExtfileExample example);

    int updateByExample(@Param("record") Extfile record, @Param("example") ExtfileExample example);

    int updateByPrimaryKeySelective(Extfile record);

    int updateByPrimaryKey(Extfile record);
}
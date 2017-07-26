package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.StdfileExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StdfileMapper {
    long countByExample(StdfileExample example);

    int deleteByExample(StdfileExample example);

    int deleteByPrimaryKey(String stdfileId);

    int insert(Stdfile record);

    int insertSelective(Stdfile record);

    List<Stdfile> selectByExample(StdfileExample example);

    Stdfile selectByPrimaryKey(String stdfileId);

    int updateByExampleSelective(@Param("record") Stdfile record, @Param("example") StdfileExample example);

    int updateByExample(@Param("record") Stdfile record, @Param("example") StdfileExample example);

    int updateByPrimaryKeySelective(Stdfile record);

    int updateByPrimaryKey(Stdfile record);
}
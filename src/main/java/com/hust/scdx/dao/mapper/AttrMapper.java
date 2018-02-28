package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.Attr;
import com.hust.scdx.model.AttrExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AttrMapper {
    long countByExample(AttrExample example);

    int deleteByExample(AttrExample example);

    int deleteByPrimaryKey(Integer attrId);

    int insert(Attr record);

    int insertSelective(Attr record);

    List<Attr> selectByExample(AttrExample example);

    Attr selectByPrimaryKey(Integer attrId);

    int updateByExampleSelective(@Param("record") Attr record, @Param("example") AttrExample example);

    int updateByExample(@Param("record") Attr record, @Param("example") AttrExample example);

    int updateByPrimaryKeySelective(Attr record);

    int updateByPrimaryKey(Attr record);
}
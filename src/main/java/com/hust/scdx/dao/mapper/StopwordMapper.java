package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.Stopword;
import com.hust.scdx.model.StopwordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StopwordMapper {
    long countByExample(StopwordExample example);

    int deleteByExample(StopwordExample example);

    int deleteByPrimaryKey(Integer stopwordId);

    int insert(Stopword record);

    int insertSelective(Stopword record);

    List<Stopword> selectByExample(StopwordExample example);

    Stopword selectByPrimaryKey(Integer stopwordId);

    int updateByExampleSelective(@Param("record") Stopword record, @Param("example") StopwordExample example);

    int updateByExample(@Param("record") Stopword record, @Param("example") StopwordExample example);

    int updateByPrimaryKeySelective(Stopword record);

    int updateByPrimaryKey(Stopword record);

	int insertBatch(List<Stopword> list);
}
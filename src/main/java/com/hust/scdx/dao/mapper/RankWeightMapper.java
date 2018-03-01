package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.RankWeight;
import com.hust.scdx.model.RankWeightExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RankWeightMapper {
    long countByExample(RankWeightExample example);

    int deleteByExample(RankWeightExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RankWeight record);

    int insertSelective(RankWeight record);

    List<RankWeight> selectByExample(RankWeightExample example);

    RankWeight selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RankWeight record, @Param("example") RankWeightExample example);

    int updateByExample(@Param("record") RankWeight record, @Param("example") RankWeightExample example);

    int updateByPrimaryKeySelective(RankWeight record);

    int updateByPrimaryKey(RankWeight record);
}
package com.hust.scdx.dao.mapper;

import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.DomainTwoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DomainTwoMapper {
	long countByExample(DomainTwoExample example);

	int deleteByExample(DomainTwoExample example);

	int deleteByPrimaryKey(String uuid);
	
	int deleteByPrimaryKeyBatch(List<String> uuid);

	int insert(DomainTwo record);

	int insertBatch(List<DomainTwo> list);

	int insertIgnore(List<DomainTwo> list);

	int insertSelective(DomainTwo record);

	List<DomainTwo> selectByExample(DomainTwoExample example);

	DomainTwo selectByPrimaryKey(String uuid);
	
	List<DomainTwo> selectByPrimaryKeyBatch(List<String> list);

	int updateByExampleSelective(@Param("record") DomainTwo record, @Param("example") DomainTwoExample example);

	int updateByExample(@Param("record") DomainTwo record, @Param("example") DomainTwoExample example);

	int updateByPrimaryKeySelective(DomainTwo record);
	
	int updateByPrimaryKeySelectiveBatch(@Param("record") DomainTwo record, @Param("example") List<String> example);

	int updateByPrimaryKey(DomainTwo record);
}
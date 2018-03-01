package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.RankWeight;
import com.hust.scdx.model.params.RankWeightQueryCondition;

public interface RankWeightService {

	List<RankWeight> selectAllWeight(int start, int limit);

	List<RankWeight> selectByCondition(RankWeightQueryCondition weight);
	
	long selectCount();
	
	long selectCountByCondition(RankWeightQueryCondition weight);

	boolean insertWeight(RankWeight weight);

	boolean updateWeight(RankWeight weight);

	boolean deleteWeightById(int id);
}

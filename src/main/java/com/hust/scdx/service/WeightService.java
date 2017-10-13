package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.Weight;
import com.hust.scdx.model.params.WeightQueryCondition;

public interface WeightService {

	List<Weight> selectAllWeight(int start, int limit);

	List<Weight> selectByCondition(WeightQueryCondition weight);
	
	long selectCount();
	
	long selectCountByCondition(WeightQueryCondition weight);

	boolean insertWeight(Weight weight);

	boolean updateWeight(Weight weight);

	boolean deleteWeightById(int id);
}

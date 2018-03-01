package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.RankWeightDao;
import com.hust.scdx.model.RankWeight;
import com.hust.scdx.model.params.RankWeightQueryCondition;
import com.hust.scdx.service.RankWeightService;
@Service
public class RankWeightServiceImpl implements RankWeightService {
	private static final Logger logger = LoggerFactory.getLogger(RankWeightServiceImpl.class);

	@Autowired
	private RankWeightDao rankWeightDao;

	@Override
	public List<RankWeight> selectAllWeight(int start, int limit) {
		List<RankWeight> weight = rankWeightDao.selectAllWeight(start, limit);
		if (weight.isEmpty()) {
			logger.info("weight is empty");
			return weight;
		}
		return weight;
	}

	@Override
	public boolean insertWeight(RankWeight weight) {
		// 添加权重数据时 ，权重名称有唯一性 。
		List<RankWeight> weights = rankWeightDao.selectWeightByName(weight.getName());
		if (!weights.isEmpty()) {
			logger.info("weightName is exist");
			return false;
		}
		int status = rankWeightDao.insertWeight(weight);
		if (status == 0) {
			logger.info("insert is error");
			return false;
		}
		return true;
	}

	/**
	 * 不能更新成已有的新闻类型 出现返回错误情况
	 */
	@Override
	public boolean updateWeight(RankWeight weight) {
		List<RankWeight> weightInfo = rankWeightDao.selectWeightById(weight.getId());
		List<RankWeight> weights = rankWeightDao.selectNotIncluedWeigth(weightInfo.get(0).getName());
		List<String> weightName = new ArrayList<String>();
		for (RankWeight weightInfos : weights) {
			weightName.add(weightInfos.getName());
		}
		if (weightName.contains(weight.getName())) {
			logger.info("update weightName is exist");
			return false;
		}
		int status = rankWeightDao.updateWeight(weight);
		if (status == 0) {
			logger.info("update weight is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteWeightById(int id) {
		int status = rankWeightDao.deleteWeightById(id);
		if (status == 0) {
			logger.info("delete is error");
			return false;
		}
		return true;
	}

	@Override
	public List<RankWeight> selectByCondition(RankWeightQueryCondition weight) {
		List<RankWeight> weights = rankWeightDao.selectByCondition(weight);
		if (weights.isEmpty()) {
			logger.info("weights is empty");
			return weights;
		}
		return weights;
	}

	@Override
	public long selectCount() {
		// TODO Auto-generated method stub		
		return rankWeightDao.selectCountOfWeight();
	}

	@Override
	public long selectCountByCondition(RankWeightQueryCondition condition) {
		// TODO Auto-generated method stub		
		return rankWeightDao.selectCountOfWeight(condition);
	}

}

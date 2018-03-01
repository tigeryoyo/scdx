package com.hust.scdx.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.RankWeightMapper;
import com.hust.scdx.model.RankWeightExample;
import com.hust.scdx.model.RankWeight;
import com.hust.scdx.model.RankWeightExample.Criteria;
import com.hust.scdx.model.params.RankWeightQueryCondition;

@Repository
public class RankWeightDao {
	@Autowired
	private RankWeightMapper rankWeightMapper;

	public int queryWeightByName(String name) {
		RankWeightExample example = new RankWeightExample();
		example.createCriteria().andNameEqualTo(name);
		List<RankWeight> list = rankWeightMapper.selectByExample(example);
		if (null == list || list.size() == 0) {
			return 0;
		}
		return list.get(0).getWeight();
	}

	public long selectCountOfWeight() {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIsNotNull();
		return rankWeightMapper.countByExample(example);
	}

	public long selectCountOfWeight(RankWeightQueryCondition weight) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(weight.getName())) {
			criteria.andNameLike("%" + weight.getName() + "%");
		}
		if (weight.getWeight() != null) {
			criteria.andWeightEqualTo(weight.getWeight());
		}
		return rankWeightMapper.countByExample(example);
	}

	public List<RankWeight> selectAllWeight(int start, int limit) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIsNotNull();
		example.setStart(start);
		example.setLimit(limit);
		List<RankWeight> weight = rankWeightMapper.selectByExample(example);
		return weight;
	}

	public List<RankWeight> selectWeightById(int id) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		List<RankWeight> weight = rankWeightMapper.selectByExample(example);
		return weight;
	}

	public List<RankWeight> selectWeightByName(String name) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(name);
		List<RankWeight> weight = rankWeightMapper.selectByExample(example);
		return weight;
	}

	public List<RankWeight> selectNotIncluedWeigth(String name) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameNotEqualTo(name);
		List<RankWeight> weight = rankWeightMapper.selectByExample(example);
		return weight;

	}

	public List<RankWeight> selectByWeightName(String weightName) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(weightName);
		List<RankWeight> weight = rankWeightMapper.selectByExample(example);
		return weight;
	}

	public int insertWeight(RankWeight weight) {
		return rankWeightMapper.insert(weight);
	}

	public int deleteWeightById(int id) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		int status = rankWeightMapper.deleteByExample(example);
		return status;
	}

	public int updateWeight(RankWeight weight) {
		return rankWeightMapper.updateByPrimaryKeySelective(weight);
	}

	public List<RankWeight> selectByCondition(RankWeightQueryCondition weight) {
		RankWeightExample example = new RankWeightExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(weight.getName())) {
			criteria.andNameLike("%" + weight.getName() + "%");
		}
		if (weight.getWeight() != null) {
			criteria.andWeightEqualTo(weight.getWeight());
		}
		if (weight.getStart() != 0) {
			example.setStart(weight.getStart());
		}
		if (weight.getLimit() != 0) {
			example.setLimit(weight.getLimit());
		}
		List<RankWeight> weights = rankWeightMapper.selectByExample(example);
		return weights;
	}
}


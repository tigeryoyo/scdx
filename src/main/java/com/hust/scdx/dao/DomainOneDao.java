package com.hust.scdx.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.DomainOneMapper;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainOneExample;
import com.hust.scdx.model.DomainOneExample.Criteria;
import com.hust.scdx.model.params.DomainOneQueryCondition;

@Repository
public class DomainOneDao {
	private static final Logger logger = LoggerFactory.getLogger(DomainOneDao.class);

	@Autowired
	private DomainOneMapper domainOneMapper;
	
	/**
	 * 根据uuid查找一级域名
	 * 
	 * @param uuid
	 * @return
	 */
	public DomainOne getDomainOneById(String uuid) {
		return domainOneMapper.selectByPrimaryKey(uuid);
	}
	
	/**
	 * 按url查询一级域名
	 * @param url
	 * @return 存在返回DomainOne 不存在则返回null
	 */
	public DomainOne getDomainOneByUrl(String url) {
		DomainOneExample example = new DomainOneExample();
		Criteria criteria = example.createCriteria();
		criteria.andUrlEqualTo(url);
		List<DomainOne> list = domainOneMapper.selectByExample(example);
		if(null == list || list.size() == 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 按时间顺序和条件分页查找一级域名，当start、limit均为0时，为查询所有符合条件的一级域名
	 * name属性为模糊查询
	 * @param condition
	 * @return
	 */
	public List<DomainOne> getDomainOneOrderByTime(DomainOneQueryCondition condition) {
		DomainOneExample example = new DomainOneExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(condition.getName())) {
			criteria.andNameLike("%" + condition.getName() + "%");
		}
		if (!StringUtils.isBlank(condition.getUrl())) {
			criteria.andUrlEqualTo(condition.getUrl());
		}
		if (!StringUtils.isBlank(condition.getColumn())) {
			criteria.andColumnEqualTo(condition.getColumn());
		}
		if (!StringUtils.isBlank(condition.getType())) {
			criteria.andTypeEqualTo(condition.getType());
		}
		if (!StringUtils.isBlank(condition.getRank())) {
			criteria.andRankEqualTo(condition.getRank());
		}
		if (!StringUtils.isBlank(condition.getIncidence())) {
			criteria.andIncidenceEqualTo(condition.getIncidence());
		}
		if (null != condition.getIsFather()) {
			criteria.andIsFatherEqualTo(condition.getIsFather());
		}
		if (null != condition.getWeight()) {
			criteria.andWeightEqualTo(condition.getWeight());
		}
		if (null != condition.getStart()) {
			example.setStart(condition.getStart());
		} else {
			example.setStart(0);
		}
		if (null != condition.getLimit()) {
			example.setLimit(condition.getLimit());
		} else {
			example.setLimit(0);
		}
		example.setOrderByClause("update_time desc,url");

		return domainOneMapper.selectByExample(example);
	}
	
	/**
	 * 查询所有一级域名的数目
	 * 
	 * @return 一级域名数
	 */
	public long getDomainOneCount() {
		DomainOneExample example = new DomainOneExample();
		Criteria criteria = example.createCriteria();
		criteria.andUuidIsNotNull();
		example.setStart(0);
		example.setLimit(0);
		return domainOneMapper.countByExample(example);
	}
	
	/**
	 * 查询符合条件的一级域名数，当start、limit均为0时，为查询所有符合条件的一级域名
	 * name属性为模糊查询
	 * @param condition
	 * @return
	 */
	public long getDomainOneCountByExample(DomainOneQueryCondition condition) {
		DomainOneExample example = new DomainOneExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(condition.getName())) {
			criteria.andNameLike("%" + condition.getName() + "%");
		}
		if (!StringUtils.isBlank(condition.getUrl())) {
			criteria.andUrlEqualTo(condition.getUrl());
		}
		if (!StringUtils.isBlank(condition.getColumn())) {
			criteria.andColumnEqualTo(condition.getColumn());
		}
		if (!StringUtils.isBlank(condition.getType())) {
			criteria.andTypeEqualTo(condition.getType());
		}
		if (!StringUtils.isBlank(condition.getRank())) {
			criteria.andRankEqualTo(condition.getRank());
		}
		if (!StringUtils.isBlank(condition.getIncidence())) {
			criteria.andIncidenceEqualTo(condition.getIncidence());
		}
		if (null != condition.getIsFather()) {
			criteria.andIsFatherEqualTo(condition.getIsFather());
		}
		if (null != condition.getWeight()) {
			criteria.andWeightEqualTo(condition.getWeight());
		}
		if (null != condition.getStart()) {
			example.setStart(condition.getStart());
		} else {
			example.setStart(0);
		}
		if (null != condition.getLimit()) {
			example.setLimit(condition.getLimit());
		} else {
			example.setLimit(0);
		}
		return domainOneMapper.countByExample(example);
	}
	
	/**
	 * 根据给定id删除域名
	 * 
	 * @param id
	 * @return
	 */
	public boolean delelteDomainOneById(String id) {
		if (0 < domainOneMapper.deleteByPrimaryKey(id))
			return true;
		else
			return false;
	}
	
	/**
	 * 根据id更新domain信息
	 * 
	 * @param domainOne
	 *            存放id和要修改的信息
	 * @return
	 */
	public boolean updateDomainOneInfo(DomainOne domainOne) {
		if (0 < domainOneMapper.updateByPrimaryKeySelective(domainOne))
			return true;
		else
			return false;
	}
	
	/**
	 * 根据给定条件查找对应域名并修改其信息
	 * 
	 * @param domainOne
	 *            要修改的信息
	 * @param condition
	 *            给定条件用来锁定要修改的域名
	 * @return
	 */
	public boolean updateDomainOneInfo(DomainOne domainOne, DomainOneQueryCondition condition) {
		DomainOneExample example = new DomainOneExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(condition.getName())) {
			criteria.andNameLike("%" + condition.getName() + "%");
		}
		if (!StringUtils.isBlank(condition.getUrl())) {
			criteria.andUrlEqualTo(condition.getUrl());
		}
		if (!StringUtils.isBlank(condition.getColumn())) {
			criteria.andColumnEqualTo(condition.getColumn());
		}
		if (!StringUtils.isBlank(condition.getType())) {
			criteria.andTypeEqualTo(condition.getType());
		}
		if (!StringUtils.isBlank(condition.getRank())) {
			criteria.andRankEqualTo(condition.getRank());
		}
		if (!StringUtils.isBlank(condition.getIncidence())) {
			criteria.andIncidenceEqualTo(condition.getIncidence());
		}
		if (null != condition.getIsFather()) {
			criteria.andIsFatherEqualTo(condition.getIsFather());
		}
		if (null != condition.getWeight()) {
			criteria.andWeightEqualTo(condition.getWeight());
		}
		if (0 < domainOneMapper.updateByExampleSelective(domainOne, example))
			return true;
		else
			return false;

	}
	
	/**
	 * 批量插入一级域名，url重复的数据直接覆盖，没有的属性会设为默认值
	 * 
	 * @param domainOnes
	 * @return
	 */
	public boolean insertDomain(List<DomainOne> domainOnes) {
		if (0 < domainOneMapper.insertBatch(domainOnes))
			return true;
		else
			return false;
	}

	/**
	 * 批量插入一级域名，url重复的数据不予处理
	 * 
	 * @param domianOnes
	 * @return
	 */
	public boolean insertIgnore(List<DomainOne> domianOnes) {
		if (0 < domainOneMapper.insertIgnore(domianOnes))
			return true;
		else
			return false;
	}

	/**
	 * 插入一条一级域名，没有的属性为默认值
	 * 
	 * @param domainOne
	 * @return 插入失败则返回0
	 */
	public boolean insertDomain(DomainOne domainOne) {
		if (0 < domainOneMapper.insertSelective(domainOne))
			return true;
		else
			return false;
	}
	
	
}

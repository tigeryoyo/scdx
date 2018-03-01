package com.hust.scdx.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.DomainTwoMapper;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.DomainTwoExample;
import com.hust.scdx.model.DomainTwoExample.Criteria;
import com.hust.scdx.model.params.DomainTwoQueryCondition;

@Repository
public class DomainTwoDao {
	private static final Logger logger = LoggerFactory.getLogger(DomainTwoDao.class);

	@Autowired
	private DomainTwoMapper domainTwoMapper;
	
	/**
	 * 按时间顺序根据给定查询条件查找符合要求的二级域名网站
	 * 
	 * @param condition
	 *            查询条件
	 * @return
	 */
	public List<DomainTwo> getDomainTwoByCondition(DomainTwoQueryCondition condition) {
		List<DomainTwo> list = new ArrayList<>();
		DomainTwoExample example = new DomainTwoExample();
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
		if (null != condition.getMaintenanceStatus()) {
			criteria.andMaintenanceStatusEqualTo(condition.getMaintenanceStatus());
		}
		if (null != condition.getFatherId()) {
			criteria.andFatherUuidEqualTo(condition.getFatherId());
		}
		if (null != condition.getWeight()) {
			criteria.andWeightEqualTo(condition.getWeight());
		}
		example.setOrderByClause("maintenance_status desc,update_time desc,url");
		list = domainTwoMapper.selectByExample(example);
		return list;
	}
	
	/**
	 * 根据父级域名id查找二级域名
	 * 
	 * @param fatherUuid
	 * @return
	 */
	public List<DomainTwo> getDomainTwoByFatherId(String fatherUuid) {
		List<DomainTwo> list = new ArrayList<>();
		DomainTwoExample example = new DomainTwoExample();
		Criteria criteria = example.createCriteria();
		criteria.andFatherUuidEqualTo(fatherUuid);
		example.setOrderByClause("maintenance_status desc,update_time desc,url");
		list = domainTwoMapper.selectByExample(example);
		return list;
	}
	
	/**
	 * 根据url查询二级域名
	 * @param url
	 * @return 存在返回DomainTwo不存在返回null
	 */
	public DomainTwo getDomainTwoByUrl(String url) {
		// TODO Auto-generated method stub
		DomainTwoExample example = new DomainTwoExample();
		Criteria criteria = example.createCriteria();
		criteria.andUrlEqualTo(url);
		List<DomainTwo> list = domainTwoMapper.selectByExample(example);
		if(null == list || list.size()== 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 根据二级域名id查找二级域名
	 * 
	 * @param uuid
	 * @return
	 */
	public DomainTwo getDomainTwoById(String uuid) {
		return domainTwoMapper.selectByPrimaryKey(uuid);
	}
	
	/**
	 * 根据二级域名id查找二级域名
	 * 
	 * @param uuid
	 * @return
	 */
	public List<DomainTwo> getDomainTwoById(List<String> uuid) {
		return domainTwoMapper.selectByPrimaryKeyBatch(uuid);
	}
	
	/**
	 * 根据二级域名id删除二级域名
	 * 
	 * @param uuid
	 * @return 删除成功返回true 删除十包返回false
	 */
	public boolean deleteDomainById(String uuid) {
		if (0 < domainTwoMapper.deleteByPrimaryKey(uuid))
			return true;
		else
			return false;
	}
	
	/**
	 * 根据二级域名id删除二级域名
	 * 
	 * @param uuid
	 * @return 删除成功返回true 删除十包返回false
	 */
	public boolean deleteDomainById(List<String> uuids) {
		if (0 < domainTwoMapper.deleteByPrimaryKeyBatch(uuids))
			return true;
		else
			return false;
	}
	
	/**
	 * 根据id和要修改的信息更新二级域名信息
	 * 
	 * @param record
	 *            Domain对象包含uuid，和其他需要修改的信息，不需要修改的信息可以不设置
	 * @return
	 */
	public boolean updateDomainTwo(DomainTwo record) {
		if (0 < domainTwoMapper.updateByPrimaryKeySelective(record))
			return true;
		else
			return false;
	}
	
	/**
	 * 根据id和要修改的信息更新二级域名信息
	 * 
	 * @param record
	 *            Domain对象包含uuid，和其他需要修改的信息，不需要修改的信息可以不设置
	 * @return
	 */
	public boolean updateDomainTwo(DomainTwo record,List<String> uuids) {
		if (0 < domainTwoMapper.updateByPrimaryKeySelectiveBatch(record,uuids))
			return true;
		else
			return false;
	}

	/**
	 * 根据给定条件查找对应域名并修改其信息
	 * 
	 * @param record
	 *            要修改的信息
	 * @param condition
	 *            给定条件用来锁定要修改的域名
	 * @return
	 */
	public boolean updateDomainTwo(DomainTwo record, DomainTwoQueryCondition condition) {
		DomainTwoExample example = new DomainTwoExample();
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
		if (null != condition.getFatherId()) {
			criteria.andFatherUuidEqualTo(condition.getFatherId());
		}
		if (null != condition.getMaintenanceStatus()) {
			criteria.andMaintenanceStatusEqualTo(condition.getMaintenanceStatus());
		}
		if (null != condition.getWeight()) {
			criteria.andWeightEqualTo(condition.getWeight());
		}
		if (0 < domainTwoMapper.updateByExampleSelective(record, example))
			return true;
		else
			return false;
	}

	/**
	 * 批量插入二级域名，重复的url直接覆盖，其中没有的属性会设为默认值
	 * 
	 * @param domainTwos
	 * @return 返回0表示插入失败
	 */
	public boolean insertDomainTwo(List<DomainTwo> domainTwos) {
		if (0 < domainTwoMapper.insertBatch(domainTwos))
			return true;
		else
			return false;
	}

	/**
	 * 批量插入二级域名，重复的url不予处理
	 * 
	 * @param domainTwos
	 * @return
	 */
	public boolean insertIgnore(List<DomainTwo> domainTwos) {
		if (0 < domainTwoMapper.insertIgnore(domainTwos))
			return true;
		else
			return false;
	}

	/**
	 * 插入二级域名
	 * 
	 * @param domainTwo
	 * @return 返回0表示插入失败
	 */
	public boolean insertDomainTwo(DomainTwo domainTwo) {
		if (0 < domainTwoMapper.insertSelective(domainTwo))
			return true;
		else
			return false;
	}
}

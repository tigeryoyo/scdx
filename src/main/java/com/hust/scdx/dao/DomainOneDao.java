package com.hust.scdx.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nlpcn.commons.lang.util.StringUtil;
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
	 * 根据uuid查找一级域名
	 * 
	 * @param uuid
	 * @return
	 */
	public List<DomainOne> getDomainOneById(List<String> uuids) {
		return domainOneMapper.selectByPrimaryKeyBatch(uuids);
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
			criteria.andUrlLike("%" +condition.getUrl() + "%");
		}
		if (!StringUtils.isBlank(condition.getColumn())) {
			criteria.andColumnLike("%" +condition.getColumn()+ "%");
		}
		if (!StringUtils.isBlank(condition.getIncidence())) {
			criteria.andIncidenceLike("%" +condition.getIncidence()+ "%");
		}
		if (null !=condition.getType()) {
			criteria.andTypeIn(condition.getType());
		}
		if (null !=condition.getRank()) {
			criteria.andRankIn(condition.getRank());
		}
		if (null != condition.getIsFather()) {
			criteria.andIsFatherEqualTo(condition.getIsFather());
		}
		if (null != condition.getMaintenanceStatus()) {
			criteria.andMaintenanceStatusEqualTo(condition.getMaintenanceStatus());
		}
		if (null != condition.getIsFather()) {
			criteria.andMaintenanceStatusEqualTo(condition.getIsFather());
		}
		if (null != condition.getWeightStart()) {
			criteria.andWeightGreaterThanOrEqualTo(condition.getWeightStart());
		}
		if (null != condition.getWeightEnd()) {
			criteria.andWeightLessThanOrEqualTo(condition.getWeightEnd());
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
		String order = "";
		if(null== condition.getTimeSorting()||condition.getTimeSorting()==1){
			order+="update_time desc";
		}else if(condition.getTimeSorting()==2){
			order+="update_time";
		}
		if(null!= condition.getUrlSorting()){
			if(condition.getUrlSorting()==1){
				order+=",url desc";
			}else if(condition.getUrlSorting()==2){
				order+=",url";
			}
		}
		if(null!= condition.getNameSorting()){
			if(condition.getNameSorting()==1){
				order+=",name desc";
			}else if(condition.getNameSorting()==2){
				order+=",name";
			}
		}
		if(null!=condition.getColumnSorting()){
			if(condition.getColumnSorting()==1){
				order+=",domain_one.column desc";
			}else if(condition.getColumnSorting()==2){
				order+=",domain_one.column";
			}
		}
		if(null!=condition.getRankSorting()){
			if(condition.getRankSorting()==1){
				order+=",rank desc";
			}else if(condition.getRankSorting()==2){
				order+=",rank";
			}
		}
		if(null!=condition.getTypeSorting()){
			if(condition.getTypeSorting()==1){
				order+=",type desc";
			}else if(condition.getTypeSorting()==2){
				order+=",type";
			}
		}
		if(null!=condition.getWeightSorting()){
			if(condition.getWeightSorting()==1){
				order+=",weight desc";
			}else if(condition.getWeightSorting()==2){
				order+=",weight";
			}
		}
		if(null!=condition.getMaintenanceSorting()){
			if(condition.getMaintenanceSorting()==1){
				order+=",maintenance_status desc";
			}else if(condition.getMaintenanceSorting()==2){
				order+=",maintenance_status";
			}
		}
		if(order.startsWith(",")){
			order = order.substring(1);
		}
		if(!StringUtil.isBlank(order))
			example.setOrderByClause(order);

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
			criteria.andUrlLike("%" +condition.getUrl() + "%");
		}
		if (!StringUtils.isBlank(condition.getColumn())) {
			criteria.andColumnLike("%" +condition.getColumn()+ "%");
		}
		if (!StringUtils.isBlank(condition.getIncidence())) {
			criteria.andIncidenceLike("%" +condition.getIncidence()+ "%");
		}
		if (null !=condition.getType()) {
			criteria.andTypeIn(condition.getType());
		}
		if (null !=condition.getRank()) {
			criteria.andRankIn(condition.getRank());
		}
		if (null != condition.getIsFather()) {
			criteria.andIsFatherEqualTo(condition.getIsFather());
		}
		if (null != condition.getMaintenanceStatus()) {
			criteria.andMaintenanceStatusEqualTo(condition.getMaintenanceStatus());
		}
		if (null != condition.getIsFather()) {
			criteria.andMaintenanceStatusEqualTo(condition.getIsFather());
		}
		if (null != condition.getWeightStart()) {
			criteria.andWeightGreaterThanOrEqualTo(condition.getWeightStart());
		}
		if (null != condition.getWeightEnd()) {
			criteria.andWeightLessThanOrEqualTo(condition.getWeightEnd());
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
		String order = "";
		if(null== condition.getTimeSorting()||condition.getTimeSorting()==1){
			order+="update_time desc";
		}else if(condition.getTimeSorting()==2){
			order+="update_time";
		}
		if(null!= condition.getUrlSorting()){
			if(condition.getUrlSorting()==1){
				order+=",url desc";
			}else if(condition.getUrlSorting()==2){
				order+=",url";
			}
		}
		if(null!= condition.getNameSorting()){
			if(condition.getNameSorting()==1){
				order+=",name desc";
			}else if(condition.getNameSorting()==2){
				order+=",name";
			}
		}
		if(null!=condition.getColumnSorting()){
			if(condition.getColumnSorting()==1){
				order+=",domain_one.column desc";
			}else if(condition.getColumnSorting()==2){
				order+=",domain_one.column";
			}
		}
		if(null!=condition.getRankSorting()){
			if(condition.getRankSorting()==1){
				order+=",rank desc";
			}else if(condition.getRankSorting()==2){
				order+=",rank";
			}
		}
		if(null!=condition.getTypeSorting()){
			if(condition.getTypeSorting()==1){
				order+=",type desc";
			}else if(condition.getTypeSorting()==2){
				order+=",type";
			}
		}
		if(null!=condition.getWeightSorting()){
			if(condition.getWeightSorting()==1){
				order+=",weight desc";
			}else if(condition.getWeightSorting()==2){
				order+=",weight";
			}
		}
		if(null!=condition.getMaintenanceSorting()){
			if(condition.getMaintenanceSorting()==1){
				order+=",maintenance desc";
			}else if(condition.getMaintenanceSorting()==2){
				order+=",maintenance";
			}
		}
		if(order.startsWith(",")){
			order = order.substring(1);
		}
		example.setOrderByClause(order);
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
	 * 根据给定id批量删除域名
	 * 
	 * @param id
	 * @return
	 */
	public boolean delelteDomainOneById(List<String> ids){
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
	 * 根据id更新domain信息
	 * 
	 * @param domainOne 要修改的信息
	 * @param uuids 要修改信息的uuid记录 
	 * @return
	 */
	public boolean updateDomainOneInfo(DomainOne domainOne,List<String> uuids) {
		if (0 < domainOneMapper.updateByPrimaryKeySelectiveBatch(domainOne,uuids))
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
	public boolean updateDomainOneInfo(DomainOne domainOne, DomainOne condition) {
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
		if (0 < domainOneMapper.insertSelective(domainOne)){
			return true;
		}
		else
			return false;
	}
	
	
}

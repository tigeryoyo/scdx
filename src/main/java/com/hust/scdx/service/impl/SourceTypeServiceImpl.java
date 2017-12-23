package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.DomainOneDao;
import com.hust.scdx.dao.DomainTwoDao;
import com.hust.scdx.dao.SourceTypeDao;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.SourceType;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;
import com.hust.scdx.model.params.SourceTypeQueryCondition;
import com.hust.scdx.service.SourceTypeService;
import com.hust.scdx.util.UrlUtil;
@Service
@Transactional
public class SourceTypeServiceImpl implements SourceTypeService {
	private static final Logger logger = LoggerFactory.getLogger(SourceTypeServiceImpl.class);
	@Autowired
	private SourceTypeDao sourceTypeDao;
	
	@Autowired
	private DomainOneDao domainOneDao;

	@Autowired
	private DomainTwoDao domainTwoDao;
	
	@Override
	public List<SourceType> selectSourceType(int start, int limit) {
		List<SourceType> sourceType = sourceTypeDao.selectSourceType(start, limit);
		if (sourceType.isEmpty()) {
			logger.info("sourceTYpe is empty");
			return null;
		}
		return sourceType;
	}

	@Override
	public List<SourceType> selectSourceTypeByName(SourceTypeQueryCondition sourceType) {
		List<SourceType> sourceTypes = sourceTypeDao.selectSourceTypeByName(sourceType);
		if (sourceTypes.isEmpty()) {
			logger.info("The name is not exist");
		}
		return sourceTypes;
	}

	@Override
	public int deleteSourceTypeById(int id) {
		String typeName = sourceTypeDao.selectSourTypeById(id).getName();
		int status = sourceTypeDao.deleteSourceTypeById(id);
		if (status == 0) {
			logger.info("delete sourceType is error");
			return status;
		}
		//类型删除成功，将类型属性为该类型的域名的类型属性清空，同时更新数据库和内存
		DomainOneQueryCondition doc = new DomainOneQueryCondition();
		doc.setType(typeName);
		DomainOne domainOne = new DomainOne();
		domainOne.setType("");
		domainOneDao.updateDomainOneInfo(domainOne, doc);
		DomainTwoQueryCondition dtc = new DomainTwoQueryCondition();
		dtc.setType(typeName);
		DomainTwo domainTwo = new DomainTwo();
		domainTwo.setType("");
		domainTwoDao.updateDomainTwo(domainTwo, dtc);
		UrlUtil.initialDomainAndType();
		return status;
	}

	@Override
	public int insertSourceType(String name) {
		int status = sourceTypeDao.insertSourceType(name);
		if (status == 0) {
			logger.info("insert sourceType is error");
			return status;
		}
		return status;
	}

	@Override
	public int updateSourceType(SourceType sourceType) {
		String oldTypeName = sourceTypeDao.selectSourTypeById(sourceType.getId()).getName();
		String newTypeName = sourceType.getName();
		int status = sourceTypeDao.updateSourceType(sourceType);
		if (status == 0) {
			logger.info("update sourcetype is error");
			return status;
		}
		if(!StringUtils.isBlank(newTypeName)){
		//类型修改成功，将类型属性为该类型的域名的类型属性也同步修改，同时更新数据库和内存
			DomainOneQueryCondition doc = new DomainOneQueryCondition();
			doc.setType(oldTypeName);
			DomainOne domainOne = new DomainOne();
			domainOne.setType(newTypeName);
			domainOneDao.updateDomainOneInfo(domainOne, doc);
			DomainTwoQueryCondition dtc = new DomainTwoQueryCondition();
			dtc.setType(oldTypeName);
			DomainTwo domainTwo = new DomainTwo();
			domainTwo.setType(newTypeName);
			domainTwoDao.updateDomainTwo(domainTwo, dtc);
			UrlUtil.initialDomainAndType();
		}
		return status;
	}
	/**
	 * 合并类型，并取一个新的类型名
	 * @param ids 待合并的类型id集合
	 * @param newName 新的类型名
	 * @return
	 */
	@Override
	public boolean mergedSourceType(int[] ids,String newName){
		List<String> names = new ArrayList<String>();
		try{
			for (Integer id : ids) {
				names.add(sourceTypeDao.selectSourTypeById(id).getName());
				sourceTypeDao.deleteSourceTypeById(id);
			}
			sourceTypeDao.insertSourceType(newName);
		}catch (Exception e){
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
		//类型合并成功，将类型属性为该类型的域名的类型属性也同步修改，同时更新数据库和内存
		DomainOneQueryCondition doc = new DomainOneQueryCondition();
		DomainOne domainOne = new DomainOne();
		domainOne.setType(newName);
		DomainTwoQueryCondition dtc = new DomainTwoQueryCondition();
		DomainTwo domainTwo = new DomainTwo();
		domainTwo.setType(newName);
		for (String name : names) {
			doc.setType(name);
			dtc.setType(name);
			domainOneDao.updateDomainOneInfo(domainOne, doc);
			domainTwoDao.updateDomainTwo(domainTwo, dtc);
		}
		UrlUtil.initialDomainAndType();
		return true;
	}

	@Override
	public long selectSourceTypeCount() {
		return sourceTypeDao.selectSourceTypeCount();
	}

	@Override
	public long selectSourceTypeCountByName(String name) {
		// TODO Auto-generated method stub
		SourceTypeQueryCondition sourceType = new SourceTypeQueryCondition();
		sourceType.setName(name);
		return sourceTypeDao.selectSourceTypeCountByName(sourceType);
	}
}

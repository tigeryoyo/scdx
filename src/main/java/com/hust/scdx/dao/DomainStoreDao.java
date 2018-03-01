package com.hust.scdx.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.DomainStoreMapper;
import com.hust.scdx.model.DomainStore;

@Repository
public class DomainStoreDao {
	private static final Logger logger = LoggerFactory.getLogger(DomainStoreDao.class);

	@Autowired
	private DomainStoreMapper domainStoreMapper;
	
	public boolean insertDomainStore(DomainStore domainStore ){
		if (0 < domainStoreMapper.insertSelective(domainStore)){
			return true;
		}
		else
			return false;
	}
	
	public boolean insertDomainStore(List<DomainStore> list){
		if(null==list || list.size()==0)return false;
		if (0 < domainStoreMapper.insertBatch(list))
			return true;
		else
			return false;
	}
}

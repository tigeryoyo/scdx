package com.hust.scdx.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.AttrDao;
import com.hust.scdx.model.Attr;
import com.hust.scdx.model.params.AttrQueryCondition;
import com.hust.scdx.service.AttrService;

@Service
public class AttrServiceImpl implements AttrService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(AttrServiceImpl.class);

	@Autowired
	private AttrDao attrDao;

	@Override
	public List<Attr> queryAllAttr() {
		return attrDao.queryAllAttr();
	}
	
	@Override
	public Long queryAttrCountByCondition(AttrQueryCondition condition) {
		return attrDao.queryAttrCount(condition);
	}
	
	@Override
	public List<Attr> queryAttrByCondition(AttrQueryCondition condition) {
		return attrDao.queryAttrByCondition(condition);
	}

	@Override
	public Attr queryAttrById(int attrId) {
		return attrDao.queryAttrById(attrId);
	}

	@Override
	public int insertAttr(String attrMainname, String attrAlias) {
		return attrDao.insertAttr(attrMainname, attrAlias);
	}

	@Override
	public int deleteAttr(int attrId) {
		return attrDao.deleteAttr(attrId);
	}

	@Override
	public int updateAttr(Attr attr) {
		return attrDao.updateAttr(attr);
	}

}

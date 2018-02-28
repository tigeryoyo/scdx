package com.hust.scdx.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.AttrDao;
import com.hust.scdx.model.Attr;
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
	public Attr queryAttrById(int attrId) {
		return attrDao.queryAttrById(attrId);
	}

}

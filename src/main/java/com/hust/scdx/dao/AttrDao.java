package com.hust.scdx.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.AttrMapper;
import com.hust.scdx.model.Attr;

@Repository
public class AttrDao {
	private static final Logger logger = LoggerFactory.getLogger(AttrDao.class);
	@Autowired
	private AttrMapper attrMapper;
	
	public Attr queryAttrById(int attrId) {
		return attrMapper.selectByPrimaryKey(attrId);
	}
}
package com.hust.scdx.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.mapper.AttrMapper;
import com.hust.scdx.model.Attr;
import com.hust.scdx.model.AttrExample;
import com.hust.scdx.model.AttrExample.Criteria;

@Repository
public class AttrDao {
	@Autowired
	private AttrMapper attrMapper;

	/**
	 * 根据attrId查找attr
	 * 
	 * @param attrId
	 * @return
	 */
	public Attr queryAttrById(int attrId) {
		return attrMapper.selectByPrimaryKey(attrId);
	}

	/**
	 * 查询所有的属性
	 * 
	 * @return
	 */
	public List<Attr> queryAllAttr() {
		AttrExample example = new AttrExample();
		Criteria criteria = example.createCriteria();
		criteria.andAttrIdIsNotNull();
		return attrMapper.selectByExample(example);
	}

	/**
	 * 插入attr
	 * 
	 * @param attrMainname
	 * @param attrAlias
	 * @return
	 */
	public int insertAttr(String attrMainname, String attrAlias) {
		Attr attr = new Attr();
		attr.setAttrMainname(attrMainname);
		attr.setAttrAlias(attrAlias);
		return attrMapper.insert(attr);
	}

	/**
	 * 根据attrId删除attr
	 * 
	 * @param attrId
	 * @return
	 */
	public int deleteAttr(int attrId) {
		// id 1~10为必要属性，不能删除。
		if (attrId <= Constant.keyAttrCount) {
			return -1;
		}
		return attrMapper.deleteByPrimaryKey(attrId);
	}

	/**
	 * 更新attr
	 * 
	 * @param attr
	 * @return
	 */
	public int updateAttr(Attr attr) {
		attrMapper.updateByPrimaryKey(attr);
		return 0;
	}
}
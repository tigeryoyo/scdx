package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.Attr;
import com.hust.scdx.model.params.AttrQueryCondition;

public interface AttrService {
	List<Attr> queryAllAttr();

	Attr queryAttrById(int attrId);

	Long queryAttrCountByCondition(AttrQueryCondition condition);
	
	List<Attr> queryAttrByCondition(AttrQueryCondition condition);
	
	int insertAttr(String attrMainname, String attrAlias);

	int deleteAttr(int attrId);

	int updateAttr(Attr attr);
}
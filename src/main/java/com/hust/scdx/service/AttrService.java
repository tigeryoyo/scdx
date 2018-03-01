package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.Attr;

public interface AttrService {
	List<Attr> queryAllAttr();

	Attr queryAttrById(int attrId);

	int insertAttr(String attrMainname, String attrAlias);

	int deleteAttr(int attrId);

	int updateAttr(Attr attr);
}
package com.hust.scdx.model;

import java.io.Serializable;

public class Attr implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer attrId;

	private String attrMainname;

	private String attrAlias;

	public Integer getAttrId() {
		return attrId;
	}

	public void setAttrId(Integer attrId) {
		this.attrId = attrId;
	}

	public String getAttrMainname() {
		return attrMainname;
	}

	public void setAttrMainname(String attrMainname) {
		this.attrMainname = attrMainname == null ? null : attrMainname.trim();
	}

	public String getAttrAlias() {
		return attrAlias;
	}

	public void setAttrAlias(String attrAlias) {
		this.attrAlias = attrAlias == null ? null : attrAlias.trim();
	}
}
package com.hust.scdx.model;

import java.io.Serializable;
import java.util.Date;

public class Topic implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String topicId;

	private String topicName;

	private String topicType;

	private String attr;

	private Date createTime;

	private String creator;

	private String lastOperator;

	private Date lastUpdateTime;

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId == null ? null : topicId.trim();
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName == null ? null : topicName.trim();
	}

	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType == null ? null : topicType.trim();
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr == null ? null : attr.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator == null ? null : creator.trim();
	}

	public String getLastOperator() {
		return lastOperator;
	}

	public void setLastOperator(String lastOperator) {
		this.lastOperator = lastOperator == null ? null : lastOperator.trim();
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
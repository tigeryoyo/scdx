package com.hust.scdx.model.params;

import java.util.Date;

public class TopicQueryCondition {
	private String topicId;
	private String topicName;
	private String topicType;
	private String attr;
	private String creater;
	private Date createStartTime;
	private Date createEndTime;
	private Date lastUpdateStartTime;
	private Date lastUpdateEndTime;
	private int pageNo;
	private int pageSize;

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreateStartTime() {
		return createStartTime;
	}

	public void setCreateStartTime(Date createStartTime) {
		this.createStartTime = createStartTime;
	}

	public Date getCreateEndTime() {
		return createEndTime;
	}

	public void setCreateEndTime(Date createEndTime) {
		this.createEndTime = createEndTime;
	}

	public Date getLastUpdateStartTime() {
		return lastUpdateStartTime;
	}

	public void setLastUpdateStartTime(Date lastUpdateStartTime) {
		this.lastUpdateStartTime = lastUpdateStartTime;
	}

	public Date getLastUpdateEndTime() {
		return lastUpdateEndTime;
	}

	public void setLastUpdateEndTime(Date lastUpdateEndTime) {
		this.lastUpdateEndTime = lastUpdateEndTime;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

}

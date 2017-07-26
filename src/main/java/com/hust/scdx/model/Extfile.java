package com.hust.scdx.model;

import java.io.Serializable;
import java.util.Date;

public class Extfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String extfileId;

	private String extfileName;

	private String topicId;

	private String sourceType;

	private Integer size;

	private Integer lineNumber;

	private Date uploadTime;

	private String creator;

	public String getExtfileId() {
		return extfileId;
	}

	public void setExtfileId(String extfileId) {
		this.extfileId = extfileId == null ? null : extfileId.trim();
	}

	public String getExtfileName() {
		return extfileName;
	}

	public void setExtfileName(String extfileName) {
		this.extfileName = extfileName == null ? null : extfileName.trim();
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId == null ? null : topicId.trim();
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType == null ? null : sourceType.trim();
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator == null ? null : creator.trim();
	}
}
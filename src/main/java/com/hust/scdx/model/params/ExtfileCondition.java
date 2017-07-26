package com.hust.scdx.model.params;

import org.springframework.web.multipart.MultipartFile;

public class ExtfileCondition {
	/**
	 * 专题id
	 */
	private String topicId;
	/**
	 * 文件内容
	 */
	private MultipartFile file;
	/**
	 * 数据类型：新闻or微博
	 */
	private String sourceType;

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}

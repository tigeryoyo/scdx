package com.hust.scdx.model;

import java.io.Serializable;
import java.util.Date;

public class Stdfile implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String stdfileId;

    private String stdfileName;

    private String topicId;

    private Integer size;

    private Integer lineNumber;

    private Date uploadTime;

    private String creator;

    public String getStdfileId() {
        return stdfileId;
    }

    public void setStdfileId(String stdfileId) {
        this.stdfileId = stdfileId == null ? null : stdfileId.trim();
    }

    public String getStdfileName() {
        return stdfileName;
    }

    public void setStdfileName(String stdfileName) {
        this.stdfileName = stdfileName == null ? null : stdfileName.trim();
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId == null ? null : topicId.trim();
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
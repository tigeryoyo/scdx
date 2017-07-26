package com.hust.scdx.model;

import java.io.Serializable;
import java.util.Date;

public class Stopword implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer stopwordId;

    private String word;

    private String creator;

    private Date createTime;

    public Integer getStopwordId() {
        return stopwordId;
    }

    public void setStopwordId(Integer stopwordId) {
        this.stopwordId = stopwordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
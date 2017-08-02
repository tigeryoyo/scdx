package com.hust.scdx.service;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.params.StdfileQueryCondition;

public interface StdfileService {

	int deleteStdfileByTopicId(String topicId);

	int insert(StdfileQueryCondition con, HttpServletRequest request);

}

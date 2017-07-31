package com.hust.scdx.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Result;

public interface ResultService {

	int insert(Result result, List<String[]> content, Map<String, Object> map);

	int deleteResultByTopicId(String topicId);


	Result queryResultById(String resultId);
	
	List<Result> queryResultByTimeRange(String topicId, Date startTime, Date endTime);

	List<String[]> getDisplayResultById(String resultId, HttpServletRequest request);

}

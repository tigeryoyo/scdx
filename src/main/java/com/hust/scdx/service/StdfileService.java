package com.hust.scdx.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.params.StdfileQueryCondition;

public interface StdfileService {

	int insert(StdfileQueryCondition con, HttpServletRequest request);

	int deleteStdfileByTopicId(String topicId);

	List<Stdfile> queryExtfilesByTimeRange(String topicId, Date startTime, Date endTime);
	
	Stdfile getLastedStdfile();

	List<String[]> analyzeByStdfileId(String stdfileId);

	Map<String, Object> getStdfileById(String stdfileId, HttpServletRequest request);
	
	Map<String, Object> statistic(String stdfileId, Integer interval,Integer targetIndex, HttpServletRequest request);

	List<String[]> analyzeByTimeRange(String topicId, Date startTime, Date endTime, HttpServletRequest request);

	Map<String, Object> getAbstractById(String topicId, String stdfileId, HttpServletRequest request);

}

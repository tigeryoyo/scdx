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

	List<String[]> analyzeByStdfileId(String stdfileId);

	Map<String, Object> getStdfileAndAbstractById(String stdfileId);

}

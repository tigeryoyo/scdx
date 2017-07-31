package com.hust.scdx.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.params.ExtfileQueryCondition;

public interface ExtfileService {

	int insert(ExtfileQueryCondition con, HttpServletRequest request);

	List<Extfile> queryExtfilesByCondtion(ExtfileQueryCondition con);

	List<String[]> getExtfilesContent(String[] extfilePaths);

	int deleteExtfileByTopicId(String topicId);

	List<String[]> miningByTimeRange(String topicId, Date startTime, Date endTime, HttpServletRequest request);
}

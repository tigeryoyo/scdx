package com.hust.scdx.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.params.ExtfileQueryCondition;

public interface ExtfileService {

	int insert(ExtfileQueryCondition con, HttpServletRequest request);

	int deleteExtfileByTopicId(String topicId);

	List<Extfile> queryExtfilesByTimeRange(ExtfileQueryCondition con);

	List<String[]> miningByTimeRange(ExtfileQueryCondition con, HttpServletRequest request);

	List<String[]> miningByExtfileIds(String topicId, List<String> extfileIds, HttpServletRequest request);

}

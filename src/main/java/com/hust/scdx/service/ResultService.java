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

	List<String[]> resetResultById(String resultId, HttpServletRequest request);
	
	/**
	 * 根据索引删除聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合
	 * @param request
	 * @return
	 */
	int deleteResultItemsByIndices(String resultId, int[] indices, HttpServletRequest request);
	
	/**
	 * 根据索引合并聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合
	 * @param request
	 * @return
	 */
	int combineResultItemsByIndices(String resultId, int[] indices, HttpServletRequest request);

	int deleteClusterItemsByIndices(String resultId, int index, int[] indices, HttpServletRequest request);

	Map<String, Object> getResultContentById(String resultId, HttpServletRequest request);
}

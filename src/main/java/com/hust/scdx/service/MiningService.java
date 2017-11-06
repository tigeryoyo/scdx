package com.hust.scdx.service;

import java.util.List;
import java.util.Map;

public interface MiningService {

	public List<List<Integer>> getOrigClusters(String[] attrs, List<String[]> content, int converterType,
			int algorithmType, int granularity);

	/**
	 * 根据聚类结果得到 聚类后类的信息（所有类（类信息（时间最早记录的index，类内所有记录数量）））
	 * 
	 * @param attrs
	 *            属性行
	 * 
	 * @param content
	 *            除了属性行以外的数据
	 * @param cluster
	 *            聚类后得到下标（与conten对应）
	 * @return
	 */
	public List<int[]> getOrigCounts(String[] attrs, List<String[]> content, List<String[]> origClusters);

	
	Map<String, Map<String, Map<String, Integer>>> statisticStdfile(List<String[]> content, int interval);
	
	Map<String, Object> getAmount(Map<String, Map<String, Map<String, Integer>>> map);
}

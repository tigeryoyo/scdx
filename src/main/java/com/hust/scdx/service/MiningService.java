package com.hust.scdx.service;

import java.util.List;

public interface MiningService {
	
	public List<List<Integer>> getOrigClusters(List<String[]> content, int converterType, int algorithmType, int granularity);

	/**
	 * 根据聚类结果得到 聚类后类的信息（所有类（类信息（时间最早记录的index，类内所有记录数量）））
	 * 
	 * @param content
	 *            需要的聚类集合
	 * @param cluster
	 *            聚类后得到下标（与conten对应）
	 * @return
	 */
	public List<int[]> getOrigCounts(List<String[]> content, List<String[]> origClusters);

}

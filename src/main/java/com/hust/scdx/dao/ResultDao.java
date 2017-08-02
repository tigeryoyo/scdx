package com.hust.scdx.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.dao.mapper.ResultMapper;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.ResultExample;
import com.hust.scdx.model.ResultExample.Criteria;
import com.hust.scdx.model.params.ResultQueryCondition;
import com.hust.scdx.util.DateConverter;
import com.hust.scdx.util.FileUtil;

@Repository
public class ResultDao {

	@Autowired
	ResultMapper resultMapper;

	/**
	 * 将生成的result记录插入mysql、将content、clusterResult、countResult插入文件系统
	 * 
	 * @param result
	 *            结果对象
	 * @param content
	 *            基础数据去重排序后的集合
	 * @param map
	 *            存储clusterResult、countResult的map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int insert(Result result, List<String[]> content, Map<String, Object> map) {
		String dirPath = DateConverter.convertToPath(result.getCreateTime()) + result.getResId();
		List<String[]> clusterResult = (List<String[]>) map.get(Cluster.ORIGCLUSTERS);
		List<String[]> countResult = (List<String[]>) map.get(Cluster.ORIGCOUNTS);
		if (FileUtil.write(DIRECTORY.CONTENT + dirPath, content) && FileUtil.write(DIRECTORY.ORIG_CLUSTER + dirPath, clusterResult)
				&& FileUtil.write(DIRECTORY.ORIG_COUNT + dirPath, countResult) && FileUtil.write(DIRECTORY.MODIFY_CLUSTER + dirPath, clusterResult)
				&& FileUtil.write(DIRECTORY.MODIFY_COUNT + dirPath, countResult)) {

			return resultMapper.insert(result);
		}

		return 0;
	}

	/**
	 * 根据resultId查找result记录
	 * 
	 * @param resultId
	 * @return
	 */
	public Result queryResultById(String resultId) {
		return resultMapper.selectByPrimaryKey(resultId);
	}

	/**
	 * 根据resultId删除Result记录以及删除相关文件。
	 * 
	 * @param resultId
	 * @return
	 */
	public int deleteByResultId(String resultId) {
		Result result = queryResultById(resultId);
		String subPath = DateConverter.convertToPath(result.getCreateTime()) + resultId;
		if (FileUtil.delete(DIRECTORY.CONTENT + subPath) && FileUtil.delete(DIRECTORY.ORIG_CLUSTER + subPath)
				&& FileUtil.delete(DIRECTORY.ORIG_COUNT + subPath) && FileUtil.delete(DIRECTORY.MODIFY_CLUSTER + subPath)
				&& FileUtil.delete(DIRECTORY.MODIFY_COUNT + subPath)) {
			return resultMapper.deleteByPrimaryKey(resultId);
		}

		return -1;
	}

	/**
	 * 根据专题id删除Result，删除专题内所有的Result记录以及文件。
	 * 
	 * @param topicId
	 * @return
	 */
	public int deleteResultByTopicId(String topicId) {
		int del = -1;
		ResultExample example = new ResultExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(topicId)) {
			criteria.andTopicIdEqualTo(topicId);
		} else {
			return del;
		}
		List<Result> list = resultMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			del = 0;
		}
		for (Result result : list) {
			del = deleteByResultId(result.getResId());
		}

		return del;
	}

	/**
	 * 查找符合条件的结果集
	 * 
	 * @param con
	 * @return
	 */
	public List<Result> queryResultsByCondtion(ResultQueryCondition con) {
		ResultExample example = new ResultExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isEmpty(con.getTopicId())) {
			criteria.andTopicIdEqualTo(con.getTopicId());
		}
		if (null != con.getStartTime()) {
			criteria.andCreateTimeGreaterThanOrEqualTo(con.getStartTime());
		}
		if (null != con.getEndTime()) {
			criteria.andCreateTimeLessThanOrEqualTo(con.getEndTime());
		}
		example.setOrderByClause("create_time desc");
		return resultMapper.selectByExample(example);
	}
}

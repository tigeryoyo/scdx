package com.hust.scdx.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.dao.mapper.ResultMapper;
import com.hust.scdx.model.Result;
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

}

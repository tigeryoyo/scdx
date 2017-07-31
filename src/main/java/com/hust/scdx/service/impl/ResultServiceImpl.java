package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.dao.ResultDao;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.params.ResultQueryCondition;
import com.hust.scdx.service.RedisService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.DateConverter;
import com.hust.scdx.util.FileUtil;

@Service
public class ResultServiceImpl implements ResultService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResultServiceImpl.class);

	@Autowired
	private ResultDao resultDao;
	@Autowired
	private RedisService redisService;

	@Override
	public int insert(Result result, List<String[]> content, Map<String, Object> map) {
		return resultDao.insert(result, content, map);
	}

	/**
	 * 根据专题id删除Result，删除专题内所有的Result记录以及文件。
	 * 
	 * @param topicId
	 * @return
	 */
	@Override
	public int deleteResultByTopicId(String topicId) {
		return resultDao.deleteResultByTopicId(topicId);
	}

	@Override
	public Result queryResultById(String resultId) {
		return resultDao.queryResultById(resultId);
	}

	/**
	 * 根据时间范围查找操作结果。
	 * 
	 * @param topicId
	 *            专题id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	@Override
	public List<Result> queryResultByTimeRange(String topicId, Date startTime, Date endTime) {
		// 根据条件查找extfile对象list
		ResultQueryCondition con = new ResultQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		List<Result> results = queryResultsByCondtion(con);
		return results;
	}

	/**
	 * 查找符合条件的所有结果。
	 * 
	 * @param con
	 * @return
	 */
	private List<Result> queryResultsByCondtion(ResultQueryCondition con) {
		return resultDao.queryResultsByCondtion(con);
	}

	/**
	 * 根据resultId查找结果，返回前台显示的list。
	 * 
	 * @param resultId
	 * @return displayResult（title、time、url、amount）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> getDisplayResultById(String resultId, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		String subPath = DateConverter.convertToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		List<int[]> origCounts = ConvertUtil.toIntList(FileUtil.read(DIRECTORY.MODIFY_COUNT + subPath));
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.error("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}

		// 返回给前端的结果list：title、url、time、amount
		List<String[]> displayResult = new ArrayList<String[]>();
		int[] indexOfEss = AttrUtil.findEssentialIndex(content.get(0));
		for (int[] row : origCounts) {
			String[] old = content.get(row[0] + 1);
			String[] sub = new String[] { old[indexOfEss[Index.TITLE]], old[indexOfEss[Index.URL]], old[indexOfEss[Index.TIME]],
					String.valueOf(row[1]) };
			displayResult.add(sub);
		}
		try {
			// 将content、orig_cluster、orig_count存入redis
			redisService.setObject(Cluster.REDIS_CONTENT, content, request);
			redisService.setObject(Cluster.REDIS_ORIGCLUSTER, FileUtil.read(DIRECTORY.ORIG_CLUSTER + subPath), request);
			redisService.setObject(Cluster.REDIS_ORIGCOUNT, FileUtil.read(DIRECTORY.ORIG_COUNT + subPath), request);
		} catch (Exception e) {
			logger.error("存储数据至redis数据库失败,请检查redis数据库是否开启。");
		}
		return displayResult;
	}

}

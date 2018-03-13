package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.constant.Constant.Resutt;
import com.hust.scdx.dao.ResultDao;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.params.ResultQueryCondition;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.RedisService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.FileUtil;
import com.hust.scdx.util.StringUtil;

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
	@Autowired
	private MiningService miningService;

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
	 * @return displayResult（title、url、time、amount）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> getDisplayResultById(String resultId, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return null;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		List<int[]> origCounts = ConvertUtil.toIntArrayList(FileUtil.read(DIRECTORY.MODIFY_COUNT + subPath));
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}

		// 返回给前端的结果list：title、url、time、amount
		List<String[]> displayResult = new ArrayList<String[]>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int[] indexOfEss = attrUtil.findEssentialIndex(content.get(0));
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
			logger.warn("存储数据至redis数据库失败,请检查redis数据库是否开启。");
		}
		return displayResult;
	}

	/**
	 * 重置聚类结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> resetResultById(String resultId, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return null;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		List<String[]> origCounts = null;
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
			origCounts = (List<String[]>) redisService.getObject(Cluster.REDIS_ORIGCOUNT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}
		if (origCounts == null || origCounts.size() == 0) {
			origCounts = FileUtil.read(DIRECTORY.ORIG_COUNT + subPath);
		}
		// 返回给前端的结果list：title、url、time、amount
		List<String[]> displayResult = new ArrayList<String[]>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int[] indexOfEss = attrUtil.findEssentialIndex(content.get(0));
		List<int[]> tmp = ConvertUtil.toIntArrayList(origCounts);
		for (int[] row : tmp) {
			String[] old = content.get(row[0] + 1);
			String[] sub = new String[] { old[indexOfEss[Index.TITLE]], old[indexOfEss[Index.URL]], old[indexOfEss[Index.TIME]],
					String.valueOf(row[1]) };
			displayResult.add(sub);
		}
		FileUtil.copy(DIRECTORY.ORIG_CLUSTER + subPath, DIRECTORY.MODIFY_CLUSTER + subPath);
		FileUtil.copy(DIRECTORY.ORIG_COUNT + subPath, DIRECTORY.MODIFY_COUNT + subPath);
		return displayResult;
	}

	/**
	 * 根据索引合并聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int combineResultItemsByIndices(String resultId, int[] indices, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return -1;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}
		List<String[]> modifyClusters = FileUtil.read(DIRECTORY.MODIFY_CLUSTER + subPath);
		if (modifyClusters.size() == 0) {
			return -1;
		}

		// 由于remove后list的长度会变，故从大往小合并，删除已合并的类。
		String[] combine = new String[0];
		for (int i = indices.length - 1; i >= 0; i--) {
			String[] cluster = modifyClusters.remove(indices[i]);
			combine = StringUtil.concat(combine, cluster);
		}
		modifyClusters.add(combine);
		// 重载排序的方法，按照降序。类中数量多的排在前面。
		Collections.sort(modifyClusters, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				return o2.length - o1.length;
			}
		});
		String[] attrs = content.get(0);
		List<String[]> modifyCounts = ConvertUtil.toStringArrayList(miningService.getOrigCounts(attrs, content, modifyClusters));

		if (FileUtil.write(DIRECTORY.MODIFY_CLUSTER + subPath, modifyClusters) && FileUtil.write(DIRECTORY.MODIFY_COUNT + subPath, modifyCounts)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 根据索引删除聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param indices
	 *            顺序的索引集合，从下标0开始
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int deleteResultItemsByIndices(String resultId, int[] indices, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return -1;
		}
		if (indices == null || indices.length == 0) {
			return 0;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> modifyClusters = FileUtil.read(DIRECTORY.MODIFY_CLUSTER + subPath);
		List<String[]> modifyCounts = FileUtil.read(DIRECTORY.MODIFY_COUNT + subPath);
		if (modifyClusters.size() == 0 || modifyCounts.size() == 0) {
			return -1;
		}
		// 由于remove后list的长度会变，故从大往小删除。
		for (int i = indices.length - 1; i >= 0; i--) {
			modifyClusters.remove(indices[i]);
			modifyCounts.remove(indices[i]);
		}

		if (FileUtil.write(DIRECTORY.MODIFY_CLUSTER + subPath, modifyClusters) && FileUtil.write(DIRECTORY.MODIFY_COUNT + subPath, modifyCounts)) {
			return 0;
		}

		return -1;
	}

	/**
	 * 根据关键词查找聚类结果中的某些类
	 * 
	 * @param resultId
	 * @param keyword
	 *            关键词
	 * @param request
	 * @return
	 */
	@Override
	public List<String[]> searchResultItemsByKeyword(String resultId, String keyword, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return null;
		}
		if (StringUtils.isBlank(keyword)) {
			return getDisplayResultById(resultId, request);
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> modifyClusters = FileUtil.read(DIRECTORY.MODIFY_CLUSTER + subPath);
		List<String[]> modifyCounts = FileUtil.read(DIRECTORY.MODIFY_COUNT + subPath);
		List<String[]> content = null;
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}
		// 返回给前端的结果list：title、url、time、amount、index
		List<String[]> displayResult = new ArrayList<String[]>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
		int[] indexOfEss = attrUtil.findEssentialIndex(content.get(0));
		String[] keywords = keyword.split("\\s+");
		int size = modifyCounts.size();
		for (int i = 0; i < size; i++) {
			String[] row = modifyCounts.get(i);
			String[] line = content.get(Integer.valueOf(row[0]) + 1);
			for (String word : keywords) {
				if (line[indexOfEss[Index.TITLE]].indexOf(word) != -1) {
					displayResult.add(new String[] { line[indexOfEss[Index.TITLE]], line[indexOfEss[Index.URL]], line[indexOfEss[Index.TIME]],
							String.valueOf(row[1]), String.valueOf(i) });
					break;
				}
			}
		}
		return displayResult;
	}

	/**
	 * 删除索引为index的类簇内的指定数据集。
	 * 
	 * @param resultId
	 *            结果id
	 * @param index
	 *            类簇索引
	 * @param indices
	 *            类簇内要删除的索引集合
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int deleteClusterItemsByIndices(String resultId, int index, int[] indices, HttpServletRequest request) {
		Result result = queryResultById(resultId);
		if (result == null) {
			return -1;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		if (content == null || content.size() == 0) {
			content = FileUtil.read(DIRECTORY.CONTENT + subPath);
		}
		List<String[]> modifyClusters = FileUtil.read(DIRECTORY.MODIFY_CLUSTER + subPath);

		String[] cluster = modifyClusters.get(index);
		String[] newCluster = new String[cluster.length - indices.length];
		for (int i : indices) {
			cluster[i] = null;
		}
		for (int i = 0, j = 0; i < cluster.length; i++) {
			if (cluster[i] != null) {
				newCluster[j++] = cluster[i];
			}
		}
		modifyClusters.set(index, newCluster);
		Collections.sort(modifyClusters, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				return o2.length - o1.length;
			}
		});
		String[] attrs = content.get(0);
		List<String[]> modifyCounts = ConvertUtil.toStringArrayList(miningService.getOrigCounts(attrs, content, modifyClusters));

		if (FileUtil.write(DIRECTORY.MODIFY_CLUSTER + subPath, modifyClusters) && FileUtil.write(DIRECTORY.MODIFY_COUNT + subPath, modifyCounts)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 根据resultId得到文件内容
	 * 
	 * @param resultId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getResultContentById(String resultId, HttpServletRequest request) {
		Map<String, Object> res = new HashMap<String, Object>();
		List<String[]> resultContent = new ArrayList<String[]>();
		Result result = queryResultById(resultId);
		if (result == null) {
			return null;
		}
		String subPath = ConvertUtil.convertDateToPath(result.getCreateTime()) + result.getResId();
		List<String[]> content = null;
		try {
			content = (List<String[]>) redisService.getObject(Cluster.REDIS_CONTENT, request);
		} catch (Exception e) {
			logger.warn("从redis数据库查找数据失败,请检查redis数据库是否开启。");
		}
		try {
			if (content == null || content.size() == 0) {
				content = FileUtil.read(DIRECTORY.CONTENT + subPath);
			}
			List<int[]> modifyClusters = ConvertUtil.toIntArrayList(FileUtil.read(DIRECTORY.MODIFY_CLUSTER + subPath));
			List<String[]> modifyCounts = FileUtil.read(DIRECTORY.MODIFY_COUNT + subPath);
			if (modifyClusters == null || modifyCounts == null) {
				return null;
			}

			resultContent.add(content.remove(0));
			for (int[] cluster : modifyClusters) {
				for (int index : cluster) {
					resultContent.add(content.get(index));
				}
				// 将类结果数量为1的聚集在结尾不以空格分开
				if (cluster.length != 1) {
					resultContent.add(new String[0]);
				}
			}
			List<Integer> marked = getMarked(modifyClusters, modifyCounts);
			Map<String, TreeMap<String, Integer>> statMap = AttrUtil.statistics(resultContent);
			res.put(Resutt.STAT, statMap);
			res.put(Resutt.RESULT, resultContent);
			res.put(Resutt.RESULTNAME, result.getResName());
			res.put(Resutt.MARKED, marked);
		} catch (Exception e) {
			logger.error("获取内容失败。");
			return null;
		}

		return res;
	}

	/**
	 * 获取时间最早的标记下标
	 * 
	 * @param content
	 * @param modifyClusters
	 * @param modifyCounts
	 * @return
	 */
	private List<Integer> getMarked(List<int[]> modifyClusters, List<String[]> modifyCounts) {
		List<Integer> marked = new ArrayList<Integer>();
		for (int i = 0; i < modifyClusters.size() - 1; i++) {
			int[] cluster = modifyClusters.get(i);
			int index = Integer.valueOf(modifyCounts.get(i)[Index.COUNT_ITEM_INDEX]);
			int amount = Integer.valueOf(modifyCounts.get(i)[Index.COUNT_ITEM_AMOUNT]);
			for (int j = 0; j < cluster.length; j++) {
				if (index == cluster[j] && amount != 1) {
					marked.add(j);
				}
			}
		}
		return marked;
	}

}

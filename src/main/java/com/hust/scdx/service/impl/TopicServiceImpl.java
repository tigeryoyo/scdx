package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.ExtfileDao;
import com.hust.scdx.dao.ResultDao;
import com.hust.scdx.dao.TopicDao;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.model.params.TopicQueryCondition;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.RedisService;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.DateConverter;

@Service
public class TopicServiceImpl implements TopicService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);
	@Autowired
	UserService userService;
	@Autowired
	TopicDao topicDao;
	@Autowired
	ExtfileDao extfileDao;
	@Autowired
	ResultDao resultDao;
	@Autowired
	RedisService redisService;
	@Autowired
	MiningService miningService;

	/**
	 * 创建专题。
	 */
	@Override
	public int createTopic(String topicName, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		Topic topic = new Topic();
		topic.setCreator(user.getUserName());
		topic.setTopicName(topicName);
		topic.setLastOperator(user.getUserName());
		topic.setCreateTime(new Date());
		topic.setLastUpdateTime(topic.getCreateTime());
		topic.setTopicId(UUID.randomUUID().toString());
		topic.setTopicType(Constant.UNKNOWN);
		return topicDao.insert(topic);
	}

	@Override
	public int deleteTopicById(String topicId) {
		return 0;
	}

	@Override
	public int updateIssueInfo(Topic topic) {
		return 0;
	}

	@Override
	public Topic queryTopicById(String topicId) {
		return topicDao.queryTopicById(topicId);
	}

	@Override
	public List<Topic> queryTopic(TopicQueryCondition con) {
		return topicDao.queryTopicByTopicName(con);
	}

	@Override
	public long queryTopicCount(TopicQueryCondition con) {
		return topicDao.queryTopicCount(con);
	}

	/**
	 * 根据时间范围聚类。
	 */
	@Override
	public List<String[]> miningByTimeRange(String topicId, Date startTime, Date endTime, HttpServletRequest request) {
		// 根据条件查找extfile对象list
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		List<Extfile> extfiles = extfileDao.queryExtfilesByCondtion(con);

		String[] extfilePaths = new String[extfiles.size()];
		int size = extfiles.size();
		for (int i = 0; i < size; i++) {
			Date uploadTime = extfiles.get(i).getUploadTime();
			extfilePaths[i] = DIRECTORY.EXTFILE + DateConverter.parseYear(uploadTime) + "/"
					+ DateConverter.parseMonth(uploadTime) + "/" + extfiles.get(i).getExtfileId();
		}

		// 读取泛数据文件集合,去重排序并取并集。
		List<String[]> content = extfileDao.getExtfilesContent(extfilePaths);
		if (content == null) {
			logger.info("content内容为空。");
			return null;
		}

		// 开始对content聚类
		User user = userService.selectCurrentUser(request);
		Map<String, Object> map = mining(content, Constant.DIGITAL, user.getAlgorithm(), user.getGranularity());
		if (map == null) {
			logger.error("聚类失败。");
			return null;
		}

		// 将此次记录插入数据库
		Result result = new Result();
		result.setCreator(user.getUserName());
		result.setCreateTime(new Date());
		result.setTopicId(topicId);
		result.setResId(UUID.randomUUID().toString());
		result.setResName(setResName(topicId, startTime, endTime));
		if (resultDao.insert(result) <= 0) {
			logger.error("插入result记录失败。");
			return null;
		}

		// 更新该条topic属性值
		Topic topic = new Topic();
		topic.setTopicId(topicId);
		topic.setLastOperator(user.getUserName());
		topic.setLastUpdateTime(new Date());
		if (topicDao.updateTopicInfo(topic) <= 0) {
			logger.error("更新topic记录失败。");
		}

		// 将content、orig_cluster、orig_count存入redis
		redisService.setObject(Constant.REDIS_CONTENT, content, request);
		redisService.setObject(Constant.REDIS_CLUSTER_RESULT, map.get(Constant.REDIS_CLUSTER_RESULT), request);
		redisService.setObject(Constant.REDIS_COUNT_RESULT, map.get(Constant.REDIS_COUNT_RESULT), request);

		return null;
	}

	// 标明 向量的装换方式，和算法。
	/**
	 * 选择算法和向量模型，对给定集合按标题排序后进行聚类
	 * 
	 * @param content
	 *            需要聚类的集合（包含属性）
	 * @param converterType
	 *            向量模型 （0-1或TF-IDF）
	 * @param algorithmType
	 *            算法（canopy、kmeans等等）
	 * @param granularity
	 *            精度（粗or细）
	 * @return 返回
	 *         排序后的集合，聚类的结果（List<String[]>所有类（类内记录下标，用空格隔开）），类的信息（List<int[]>类内最早的一条记录index，类记录个数）
	 */
	private Map<String, Object> mining(List<String[]> content, int converterType, int algorithmType, int granularity) {
		List<String[]> tmp = new ArrayList<String[]>(content);
		// 聚类
		List<List<Integer>> clusterResult = miningService.cluster(tmp, converterType, algorithmType, granularity);
		// 每个String[]都是某个类簇的数据ID的集合。
		List<String[]> cluster = ConvertUtil.toStringListB(clusterResult);
		List<String[]> countResult = ConvertUtil.toStringList(miningService.count(tmp, cluster));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.CLUSTERRESULT, ConvertUtil.toStringListB(clusterResult));
		result.put(Constant.COUNTRESULT, countResult);
		return result;
	}

	/**
	 * 根据标准命名规则命名下载的结果数据
	 * 
	 * @param topicId
	 *            专题id
	 * @param startTime
	 *            起始时间
	 * @param endTime
	 *            终结时间
	 * @return
	 */
	private String setResName(String topicId, Date startTime, Date endTime) {
		return queryTopicById(topicId).getTopicName() + DateConverter.parseMdH(startTime) + "-"
				+ DateConverter.parseMdH(endTime);
	}

	/**
	 * 对以去重好的待聚类集合按照标题进行重排序
	 * 
	 * @param filteredContent
	 *            去重后的待聚类集合(不带标题)
	 * @param titleIndex
	 *            去重后的待聚类集合中的标题下标
	 * @return
	 */
	private List<String[]> resortContent(List<String[]> filteredContent, int titleIndex) {
		List<String[]> newContent = new ArrayList<String[]>();
		newContent.addAll(filteredContent);
		newContent.remove(0);
		// 排序
		Collections.sort(newContent, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return (o1[titleIndex]).compareTo(o2[titleIndex]);
			}
		});
		newContent.add(0, filteredContent.get(0));
		return newContent;
	}
}

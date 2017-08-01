package com.hust.scdx.service.impl;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.Algorithm;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.dao.ExtfileDao;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.RedisService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.DateConverter;
import com.hust.scdx.util.ExcelUtil;

@Service
public class ExtfileServiceImpl implements ExtfileService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExtfileServiceImpl.class);

	@Autowired
	private ExtfileDao extfileDao;
	@Autowired
	private UserService userService;
	@Autowired
	private TopicService topicService;
	@Autowired
	ResultService resultService;
	@Autowired
	RedisService redisService;
	@Autowired
	MiningService miningService;

	@Override
	public int insert(ExtfileQueryCondition con, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		MultipartFile file = con.getFile();
		List<String[]> list = null;
		try {
			list = ExcelUtil.readOrigfile(file.getOriginalFilename(), file.getInputStream());
		} catch (IOException e) {
			logger.error("读取原始文件出现异常\t" + e.toString());
			return 0;
		}
		Extfile extfile = new Extfile();
		extfile.setCreator(user.getTrueName());
		extfile.setUploadTime(new Date());
		extfile.setExtfileName(file.getOriginalFilename());
		extfile.setLineNumber(list.size());
		extfile.setSize((int) (file.getSize() / 1024));
		extfile.setTopicId(con.getTopicId());
		extfile.setExtfileId(UUID.randomUUID().toString());
		// extfile.setSourceType(con.getSourceType());
		extfile.setSourceType(Constant.UNKNOWN);
		return extfileDao.insert(extfile, list);
	}

	/**
	 * 查找符合条件的基础数据对象集合
	 */
	@Override
	public List<Extfile> queryExtfilesByCondtion(ExtfileQueryCondition con) {
		return extfileDao.queryExtfilesByCondtion(con);
	}

	/**
	 * 根据专题id删除该专题下的所有基础数据:数据库与文件系统内的数据。
	 * 
	 * @param topicId
	 * @return
	 */
	@Override
	public int deleteExtfileByTopicId(String topicId) {
		return extfileDao.deleteExtfileByTopicId(topicId);
	}

	/**
	 * 根据时间范围聚类。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> miningByTimeRange(String topicId, Date startTime, Date endTime, HttpServletRequest request) {
		// 根据时间范围查找extfile对象list
		List<Extfile> extfiles = queryExtfilesByTimeRange(topicId, startTime, endTime);

		String[] extfilePaths = new String[extfiles.size()];
		int size = extfiles.size();
		for (int i = 0; i < size; i++) {
			Date uploadTime = extfiles.get(i).getUploadTime();
			extfilePaths[i] = DIRECTORY.EXTFILE + DateConverter.convertToPath(uploadTime) + extfiles.get(i).getExtfileId();
		}

		// 读取基础数据文件集合,去重排序并取并集。
		List<String[]> content = getExtfilesContent(extfilePaths);
		if (content == null) {
			logger.info("content内容为空。");
			return null;
		}

		// 开始对content聚类
		User user = userService.selectCurrentUser(request);
		Map<String, Object> map = mining(content, Algorithm.DIGITAL, user.getAlgorithm(), user.getGranularity());
		if (map.isEmpty()) {
			logger.error("聚类失败。");
			return null;
		}

		// 将此次记录插入数据库、将content、clusterResult、countResult存入文件系统
		Result result = new Result();
		result.setCreator(user.getUserName());
		result.setCreateTime(new Date());
		result.setTopicId(topicId);
		result.setResId(UUID.randomUUID().toString());
		result.setResName(setResName(topicId, startTime, endTime));
		if (resultService.insert(result, content, map) <= 0) {
			logger.error("插入result记录失败。");
			return null;
		}

		// 此次聚类的resultId存进session里。
		request.getSession().setAttribute(Cluster.RESULTID, result.getResId());

		// 更新该条topic属性值
		Topic topic = new Topic();
		topic.setTopicId(topicId);
		topic.setLastOperator(user.getUserName());
		topic.setLastUpdateTime(new Date());
		if (topicService.updateTopicInfo(topic) <= 0) {
			logger.error("更新topic记录失败。");
		}

		try {
			// 将content、orig_cluster、orig_count存入redis
			redisService.setObject(Cluster.REDIS_CONTENT, content, request);
			redisService.setObject(Cluster.REDIS_ORIGCLUSTER, map.get(Cluster.ORIGCLUSTERS), request);
			redisService.setObject(Cluster.REDIS_ORIGCOUNT, map.get(Cluster.ORIGCOUNTS), request);
		} catch (Exception e) {
			logger.warn("存储数据至redis数据库失败,请检查redis数据库是否开启。");
		}

		return (List<String[]>) map.get(Cluster.DISPLAYRESULT);
	}

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
		Map<String, Object> result = new HashMap<String, Object>();
		// 聚类,每个String[]都是某个类簇的数据ID的集合。
		List<String[]> origClusters = ConvertUtil.toStringListB(miningService.getOrigClusters(content, converterType, algorithmType, granularity));
		result.put(Cluster.ORIGCLUSTERS, origClusters);

		List<int[]> origCounts = miningService.getOrigCounts(content, origClusters);
		// 返回给前端的结果list：title、url、time、amount
		List<String[]> displayResult = new ArrayList<String[]>();
		int[] indexOfEss = AttrUtil.findEssentialIndex(content.get(0));
		for (int[] row : origCounts) {
			String[] old = content.get(row[0] + 1);
			String[] sub = new String[] { old[indexOfEss[Index.TITLE]], old[indexOfEss[Index.URL]], old[indexOfEss[Index.TIME]],
					String.valueOf(row[1]) };
			displayResult.add(sub);
		}
		result.put(Cluster.DISPLAYRESULT, displayResult);

		List<String[]> tmp = ConvertUtil.toStringList(origCounts);
		result.put(Cluster.ORIGCOUNTS, tmp);
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
		return topicService.queryTopicById(topicId).getTopicName() + DateConverter.convert(startTime) + "-" + DateConverter.convert(endTime);
	}

	/**
	 * 根据时间范围查找基础文件。
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
	public List<Extfile> queryExtfilesByTimeRange(String topicId, Date startTime, Date endTime) {
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		return queryExtfilesByCondtion(con);
	}

	/**
	 * 根据基础数据文件名集合获取基础数据文件内容，整合、去重。
	 * 
	 * @param extfilePaths
	 * @return
	 */
	@Override
	public List<String[]> getExtfilesContent(String[] extfilePaths) {
		return extfileDao.getExtfilesContent(extfilePaths);
	}

}

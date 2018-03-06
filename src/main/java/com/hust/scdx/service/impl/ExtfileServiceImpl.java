package com.hust.scdx.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Constant.Algorithm;
import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.constant.Constant.Index;
import com.hust.scdx.dao.ExtfileDao;
import com.hust.scdx.model.Attr;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.Result;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.service.AttrService;
import com.hust.scdx.service.DomainService;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.RedisService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
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
	private AttrService attrService;
	@Autowired
	private DomainService domainService;
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
		//处理已知但未维护的域名信息
		domainService.addUnMaintainedFromOrigFile(new ArrayList<>(list));
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
	public List<Extfile> queryExtfilesByTimeRange(ExtfileQueryCondition con) {
		return extfileDao.queryExtfilesByCondtion(con);
	}

	/**
	 * 根据时间范围聚类。
	 */
	@Override
	public List<String[]> miningByTimeRange(ExtfileQueryCondition con, HttpServletRequest request) {
		// 根据时间范围查找extfile对象list
		List<Extfile> extfiles = queryExtfilesByTimeRange(con);
		String[] extfilePaths = new String[extfiles.size()];
		int size = extfiles.size();
		for (int i = 0; i < size; i++) {
			Date uploadTime = extfiles.get(i).getUploadTime();
			extfilePaths[i] = DIRECTORY.EXTFILE + ConvertUtil.convertDateToPath(uploadTime) + extfiles.get(i).getExtfileId();
		}

		return mining(con, getExtfilesContent(extfilePaths), request);
	}

	/**
	 * 根据extfileIds聚类
	 */
	@Override
	public List<String[]> miningByExtfileIds(String topicId, List<String> extfileIds, HttpServletRequest request) {
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		int size = extfileIds.size();
		String[] extfilePaths = new String[size];
		for (int i = 0; i < size; i++) {
			Extfile extfile = extfileDao.queryExtfileById(extfileIds.get(i));
			Date uploadTime = extfile.getUploadTime();
			if (i == 0) {
				con.setEndTime(uploadTime);
			}
			if (i == size - 1) {
				con.setStartTime(uploadTime);
			}
			extfilePaths[i] = DIRECTORY.EXTFILE + ConvertUtil.convertDateToPath(uploadTime) + extfile.getExtfileId();
		}
		return mining(con, getExtfilesContent(extfilePaths), request);
	}

	/**
	 * 对content进行聚类
	 * 
	 * @param content
	 *            去重排序并取并集后基础文件的内容。
	 * @param user
	 * @return
	 */
	private List<String[]> mining(ExtfileQueryCondition con, List<String[]> content, HttpServletRequest request) {
		if (content == null) {
			logger.info("content内容为空。");
			return null;
		}
		// 属性行提取出来
		String[] attrs = content.remove(0);
		User user = userService.selectCurrentUser(request);
		// 开始聚类
		List<List<Integer>> origRess = multipleMining(user, attrs, content);
		if (origRess == null) {
			logger.error("聚类失败。");
			return null;
		}
		// 若参与聚类的数据条数大于一个切片长度
		if (content.size() > Constant.slices) {
			List<String[]> newContent = new ArrayList<String[]>();
			int csize = origRess.size();
			List<Integer> sub;
			int index;
			for (int i = 0; i < csize; i++) {
				sub = origRess.get(i);
				index = sub.get(0);
				newContent.add(content.get(index));
			}
			List<List<Integer>> newOrigRess = multipleMining(user, attrs, newContent);
			if (newOrigRess == null) {
				logger.error("聚类失败。");
				return null;
			}

			List<List<Integer>> tmp = new ArrayList<List<Integer>>(origRess);
			origRess.clear();
			csize = newOrigRess.size();
			for (int i = 0; i < csize; i++) {
				sub = newOrigRess.get(i);
				List<Integer> item = new ArrayList<Integer>();
				for (int _index : sub) {
					item.addAll(tmp.get(_index));
				}
				origRess.add(item);
			}
		}
		Map<String, Object> map = clean(attrs, content, origRess);
		if (map.isEmpty()) {
			logger.error("聚类失败。");
			return null;
		}

		// 将此次记录插入数据库、将content、clusterResult、countResult存入文件系统
		Result result = new Result();
		result.setCreator(user.getUserName());
		result.setCreateTime(new Date());
		result.setTopicId(con.getTopicId());
		result.setResId(UUID.randomUUID().toString());
		result.setResName(setResName(con.getTopicId(), con.getStartTime(), con.getEndTime()));
		content.add(0, attrs);
		if (resultService.insert(result, content, map) <= 0) {
			logger.error("插入result记录失败。");
			return null;
		}

		// 此次聚类的resultId存进session里。
		request.getSession().setAttribute(Cluster.RESULTID, result.getResId());

		// 更新该条topic属性值
		Topic topic = new Topic();
		topic.setTopicId(con.getTopicId());
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
	 * 多线程聚类
	 * 
	 * @param user
	 * @param attrs
	 *            属性行
	 * @param content
	 *            除了属性行的全部数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<List<Integer>> multipleMining(User user, String[] attrs, List<String[]> content) {
		List<List<Integer>> origRess = new ArrayList<List<Integer>>();
		int csize = content.size();
		int fromIndex, toIndex;
		// len为切片个数
		int len = csize / Constant.slices + (csize % Constant.slices == 0 ? 0 : 1);
		// 参数为处理聚类的线程个数
		ExecutorService excutorService = Executors.newFixedThreadPool(len > Constant.threads ? Constant.threads : len);
		Future<List<List<Integer>>>[] tasks = new Future[len];
		List<String[]> subContent;
		for (int i = 0; i < len; i++) {
			fromIndex = i * Constant.slices;
			toIndex = (i == len - 1 ? csize : (i + 1) * Constant.slices);
			subContent = content.subList(fromIndex, toIndex);
			tasks[i] = excutorService.submit(new MiningThread(attrs, subContent, i, Algorithm.DIGITAL, user.getAlgorithm(), user.getGranularity()));
		}

		for (int i = 0; i < len; i++) {
			try {
				origRess.addAll(tasks[i].get());
			} catch (Exception e) {
				logger.error("多线程聚类失败。");
				return null;
			}
		}
		excutorService.shutdown();
		return origRess;
	}

	/**
	 * 
	 * @param attrs
	 *            属性行
	 * @param content
	 *            除了属性行的内容
	 * @param origRess
	 *            聚类结果
	 * @return
	 */
	private Map<String, Object> clean(String[] attrs, List<String[]> content, List<List<Integer>> origRess) {
		Map<String, Object> result = new HashMap<String, Object>();
		AttrUtil attrUtil = AttrUtil.getSingleton();
    	int indexOfTitle = attrUtil.findIndexOf(attrs, attrUtil.getTitle_alias());
		int indexOfUrl = attrUtil.findIndexOf(attrs, attrUtil.getUrl_alias());
		int indexOfTime = attrUtil.findIndexOf(attrs, attrUtil.getTime_alias());
		// 重载排序的方法，按照降序。类中数量多的排在前面。
		Collections.sort(origRess, new Comparator<List<Integer>>() {
			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				// TODO Auto-generated method stub
				return o2.size() - o1.size();
			}
		});
		// 对于类内的排序，则先按照时间先后，再按照标题排序。
		for (List<Integer> set : origRess) {
			Collections.sort(set, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					// 先按照标题顺序
					int compare = content.get(o1)[indexOfTitle].compareTo(content.get(o2)[indexOfTitle]);
					// 若标题相同，使用时间进行排序。
					if (compare == 0) {
						compare = content.get(o1)[indexOfTime].compareTo(content.get(o2)[indexOfTime]);
					}
					return compare;
				}
			});
		}

		// 每个String[]都是某个类簇的数据ID的集合。
		List<String[]> origClusters = ConvertUtil.toStringArrayListB(origRess);
		result.put(Cluster.ORIGCLUSTERS, origClusters);

		List<int[]> origCounts = miningService.getOrigCounts(attrs, content, origClusters);
		// 返回给前端的结果list：title、url、time、amount
		List<String[]> displayResult = new ArrayList<String[]>();
		for (int[] row : origCounts) {
			String[] old = content.get(row[0]);
			String[] sub = new String[] { old[indexOfTitle], old[indexOfUrl], old[indexOfTime], String.valueOf(row[1]) };
			displayResult.add(sub);
		}
		result.put(Cluster.DISPLAYRESULT, displayResult);

		List<String[]> tmp = ConvertUtil.toStringArrayList(origCounts);
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
		return topicService.queryTopicById(topicId).getTopicName() + ConvertUtil.convertDateToName(startTime) + "-"
				+ ConvertUtil.convertDateToName(endTime);
	}

	/**
	 * 根据基础数据文件名集合获取基础数据文件内容，整合、去重。
	 * 
	 * @param extfilePaths
	 * @return
	 */
	private List<String[]> getExtfilesContent(String[] extfilePaths) {
		return extfileDao.getExtfilesContent(extfilePaths);
	}

	/**
	 * 聚类线程：对subContent进行聚类
	 * 
	 * @author tigerto
	 *
	 */
	class MiningThread implements Callable<List<List<Integer>>> {

		String[] attrs;
		List<String[]> subContent;
		int index;
		int converterType;
		int algorithmType;
		int granularity;

		public MiningThread(String[] attrs, List<String[]> subContent, int index, int converterType, int algorithmType, int granularity) {
			this.attrs = attrs;
			this.subContent = subContent;
			this.index = index;
			this.converterType = converterType;
			this.algorithmType = algorithmType;
			this.granularity = granularity;
		}

		@Override
		public List<List<Integer>> call() throws Exception {
			List<List<Integer>> result = new ArrayList<List<Integer>>();
			List<List<Integer>> res = miningService.getOrigClusters(attrs, subContent, converterType, algorithmType, granularity);
			int resSize = res.size();
			int subSize;
			List<Integer> newSub, subList;
			for (int i = 0; i < resSize; i++) {
				newSub = new ArrayList<Integer>();
				subList = res.get(i);
				subSize = subList.size();
				for (int j = 0; j < subSize; j++) {
					newSub.add(subList.get(j) + index * Constant.slices);
				}
				result.add(newSub);
			}
			return result;
		}
	}

}

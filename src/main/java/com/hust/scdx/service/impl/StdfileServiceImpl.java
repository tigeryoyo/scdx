package com.hust.scdx.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Constant.AttrID;
import com.hust.scdx.constant.Constant.Interval;
import com.hust.scdx.constant.Constant.KEY;
import com.hust.scdx.constant.Constant.StdfileMap;
import com.hust.scdx.constant.Constant.WordFont;
import com.hust.scdx.dao.StdfileDao;
import com.hust.scdx.dao.TopicDao;
import com.hust.scdx.model.Domain;
import com.hust.scdx.model.Stdfile;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.StdfileQueryCondition;
import com.hust.scdx.service.MiningService;
import com.hust.scdx.service.StdfileService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ConvertUtil;
import com.hust.scdx.util.ExcelUtil;
import com.hust.scdx.util.FileUtil;
import com.hust.scdx.util.TimeUtil;
import com.hust.scdx.util.UrlUtil;
import com.hust.scdx.util.WordUtil;
import com.hust.scdx.util.WordUtil.Env;
import com.hust.scdx.util.crawler;
import com.hust.summary.Summary;

@Service
public class StdfileServiceImpl implements StdfileService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StdfileServiceImpl.class);

	@Autowired
	TopicDao topicDao;
	@Autowired
	StdfileDao stdfileDao;
	@Autowired
	private UserService userService;
	@Autowired
	private MiningService miningService;

	/**
	 * 根据专题id删除该专题下的所有标准数据:数据库与文件系统内的数据。
	 * 
	 * @param topicId
	 * @return
	 */
	@Override
	public int deleteStdfileByTopicId(String topicId) {
		return stdfileDao.deleteStdfileByTopicId(topicId);
	}

	/**
	 * 上传准数据文件
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public int insert(StdfileQueryCondition con, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		MultipartFile file = con.getFile();
		List<String[]> list = null;
		try {// 处理时间的不准确性。
			list = ExcelUtil.readStdfile(file.getOriginalFilename(), file.getInputStream());
		} catch (IOException e) {
			logger.error("读取原始文件出现异常\t" + e.toString());
			return 0;
		}
		Stdfile stdfile = new Stdfile();
		stdfile.setCreator(user.getTrueName());
		stdfile.setUploadTime(new Date());
		stdfile.setStdfileName(file.getOriginalFilename());
		stdfile.setLineNumber(list.size());
		stdfile.setSize((int) (file.getSize() / 1024));
		stdfile.setTopicId(con.getTopicId());
		stdfile.setStdfileId(UUID.randomUUID().toString());
		return stdfileDao.insert(stdfile, list);
	}

	/**
	 * 根据时间范围查找标准数据文件。
	 * 
	 * @param topicId
	 *            专题id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param request
	 * @return
	 */
	@Override
	public List<Stdfile> queryExtfilesByTimeRange(String topicId, Date startTime, Date endTime) {
		StdfileQueryCondition con = new StdfileQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		return stdfileDao.queryStdfilesByCondtion(con);
	}

	/**
	 * 根据标准数据id分析标准数据 title、url、time、amount
	 */
	@Override
	public List<String[]> analyzeByStdfileId(String stdfileId) {
		Stdfile stdfile = stdfileDao.queryStdfileById(stdfileId);
		String stdfilePath = DIRECTORY.STDFILE + ConvertUtil.convertDateToPath(stdfile.getUploadTime()) + stdfileId;
		return FileUtil.getStdfileDisplaylist(stdfilePath);
	}

	/**
	 * 根据stdfileId得到标准文件
	 */
	@Override
	public Map<String, Object> getStdfileById(String stdfileId) {
		Stdfile stdfile = stdfileDao.queryStdfileById(stdfileId);
		String stdfilePath = DIRECTORY.STDFILE + ConvertUtil.convertDateToPath(stdfile.getUploadTime()) + stdfileId;
		Map<String, Object> stdfileMap = FileUtil.getStdfileExcelcontent(stdfilePath);
		String stdfileName = stdfile.getStdfileName();
		stdfileName = stdfileName.substring(0, stdfileName.lastIndexOf("."));
		stdfileMap.put(StdfileMap.NAME, stdfileName);
		@SuppressWarnings("unchecked")
		Map<String, TreeMap<String, Integer>> statMap = AttrUtil
				.statistics((List<String[]>) stdfileMap.get(StdfileMap.CONTENT), Constant.existDomain);
		stdfileMap.put(StdfileMap.STAT, statMap);
		return stdfileMap;
	}

	/**
	 * 根据topicId和stdfileId获取摘要
	 */
	@Override
	public Map<String, Object> getAbstractById(String topicId, String stdfileId) {
		Map<String, Object> stdfileMap = getStdfileById(stdfileId);
		stdfileMap.put(StdfileMap.REPORT, generateReport(topicId, stdfileMap));
		return stdfileMap;
	}

	/**
	 * 出图----统计准数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> statistic(String stdfileId, Integer interval, Integer targetIndex,
			HttpServletRequest request) {
		try {
			// 标准数据
			Stdfile stdfile = stdfileDao.queryStdfileById(stdfileId);
			String stdfilePath = DIRECTORY.STDFILE + ConvertUtil.convertDateToPath(stdfile.getUploadTime()) + stdfileId;
			List<String[]> cluster = FileUtil.getStdfileTargetCluster(stdfilePath, targetIndex);
			if (cluster == null || cluster.isEmpty()) {
				return null;
			}
			Map<String, Map<String, Map<String, Integer>>> timeMap = miningService.statisticStdfile(cluster, interval);
			Map<String, Object> reMap = miningService.getAmount(timeMap);
			Map<String, Integer> levelMap = (Map<String, Integer>) reMap.get(KEY.MINING_AMOUNT_MEDIA);
			Map<String, Integer> typeMap = (Map<String, Integer>) reMap.get(KEY.MINING_AMOUNT_TYPE);
			Map<String, Object> map = Maps.newHashMap();
			map.put("time", timeMap);
			Map<String, Object> countMap = Maps.newHashMap();
			countMap.put("type", typeMap);
			countMap.put("level", levelMap);
			map.put("count", countMap);
			return map;
		} catch (Exception e) {
			logger.error("exception occur when statistic:{}", e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 产生核心报告
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private XWPFDocument generateReport(String topicId, Map<String, Object> stdfileMap) {
		try {
			String topicName = topicDao.queryTopicById(topicId).getTopicName();

			WordUtil wu = new WordUtil();
			wu.addParaText("(" + topicName + ")专供", new Env().bold(true).fontType(WordFont.KAITI));
			wu.addParaText("舆情参阅", new Env().fontSize(52).bold(true).fontType(WordFont.XINSONGTI)
					.fontColor(WordFont.GREEN).alignment("center"));
			Env titleEnv = new Env().fontSize(22).bold(true).fontType(WordFont.SONGTI);
			Env mainEnv = new Env().fontType(WordFont.FANGSONG);
			Env mainBEnv = new Env(mainEnv).bold(true).fontType(WordFont.SONGTI);
			Env mainRBEnv = new Env(mainBEnv).fontColor(WordFont.RED);
			Env mainSEnv = new Env(mainEnv).fontSize(10);

			// 类簇内容，类簇与类簇之间以new String[0]区分
			List<String[]> tmp = (List<String[]>) stdfileMap.get(StdfileMap.CONTENT);
			// 属性行
			String[] attrs = tmp.remove(0);
			int indexOfTitle = AttrUtil.findIndexOfTitle(attrs);
			int indexOfTime = AttrUtil.findIndexOfTime(attrs);
			// 将类簇内容转换每一个类簇为一个list
			List<List<String[]>> content = convert(tmp);
			// 标记，每一个类簇的最早出现的新闻index
			List<Integer> marked = (List<Integer>) stdfileMap.get(StdfileMap.MARKED);
			HashMap<String, TreeMap<String, Integer>> statMap = (HashMap<String, TreeMap<String, Integer>>) stdfileMap
					.get(StdfileMap.STAT);
			TreeMap<String, Integer> timeMap = statMap.get(AttrID.TIME);
			TreeMap<String, Integer> typeMap = statMap.get(AttrID.TYPE);
			// 信息总数
			int total = 0;
			// 信息平均数
			int avg = 0;
			// 信息峰值日期
			String peakD = "";
			// 信息峰值数量
			int peakC = 0;
			// 信息峰值占比
			float peak = 0.0f;
			// 信息起止时间
			String first = timeMap.pollFirstEntry().getKey();
			String last = timeMap.pollLastEntry().getKey();
			for (Map.Entry<String, Integer> entry : timeMap.entrySet()) {
				int count = entry.getValue();
				if (count > peakC) {
					peakC = count;
					peakD = entry.getKey();
				}
				total += count;
			}
			avg = total / timeMap.size();
			peak = Math.round((float) peakC / total * 100);

			// 一周概况
			wu.addParaText("一周概况", titleEnv);
			wu.addParaText("本周互联网相关舆情信息更新量" + total + " 条（日均 ）" + avg + " 条）与上周相比，信息量上升/下降", mainEnv);
			wu.appendParaText("未发现“正/负”面舆情", mainBEnv);

			wu.setBreak();

			// 舆情聚焦
			wu.addParaText("舆情聚焦", titleEnv);
			int len = content.size();
			for (int i = 0; i < len; i++) {
				String[] row = content.get(i).get(marked.get(i));
				wu.addParaText((i + 1) + ". " + row[indexOfTitle], mainBEnv);
				wu.addParaText("（相关新闻" + content.get(i).size() + "条。）", mainSEnv);
			}
			wu.addParaText("行业舆情", titleEnv);

			wu.setBreak();

			// 监测信息量日分布情况
			wu.addParaText("监测信息量日分布情况", titleEnv);
			wu.addParaText(
					first + " 至" + last + "，通过四川电信互联网舆情信息服务平台监测数据显示，本时间段内与“" + topicName + "”相关互联网信息" + total + " ，条，",
					mainEnv);
			wu.appendParaText("未发现“正/负”面舆情。", mainBEnv);
			wu.addParaText("信息具体情况如下：", mainEnv);
			wu.addParaText("监测数据显示，" + topicName + first + " 至 " + last + "相关信息总量 " + total + " 条，平均每日信息量为 " + avg
					+ " 条。其中，" + peakD + "当天的相关信息量是本周最大峰值，相关信息共有 " + peakC + " 条，占一周信息量的 ", mainEnv);
			wu.appendParaText(peak + "%", mainRBEnv);
			wu.appendParaText("，主要为", mainEnv);
			List<String[]> theDayInfo = statTheDay(content, marked, peakD, indexOfTitle, indexOfTime);
			for (int i = 0; i < 3 && i < theDayInfo.size(); i++) {
				String[] theday = theDayInfo.get(i);
				wu.appendParaText("《" + theday[0] + "》", mainBEnv);
				wu.addParaText("（" + theday[1] + "条）", mainSEnv);
			}
			wu.addParaText("，相关转载转播。", mainEnv);

			wu.setBreak();
			// 舆情聚焦（摘要）
			wu.addParaText("舆情聚焦", titleEnv);
			List<String[]> summary = new ArrayList<>();
			summary = generateSummary(attrs, content,marked,topicName);
			int num = 1;
			for (String[] strings : summary) {
				wu.addParaText((num++) + ". " + strings[0], mainBEnv);
				wu.addParaText("        " + strings[1] + strings[2], mainEnv);
			}

			wu.setPageBreak();

			Env env1 = new Env().fontType(WordFont.KAITI);
			Env env2 = new Env(env1).alignment("right");
			Env env3 = new Env(env1).fontSize(16).bold(true);
			wu.addParaText("（责任编辑：汪静远）", env2);
			wu.addParaText("四川电信互联网舆情信息服务中心 ", env3);
			wu.addParaText("声明：", env3);
			wu.appendParaText(
					"以上舆情信息仅供参考，用户对于舆情信息所反映出的问题或状况的处理，应综合其他信息加以判断和利用，仅凭以上舆情信息做出判断、进行决策等处理措施造成不利后果及损失的，我单位不承担任何责任。",
					env1);
			return wu.getDoc();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("产生报告出错!{}", e.toString());
		}
		return null;
	}

	/**
	 * 将以new String[0]区分类簇的content转换为以list区分
	 * 
	 * @return
	 */
	private List<List<String[]>> convert(List<String[]> content) {
		List<List<String[]>> list = new ArrayList<List<String[]>>();
		int len = content.size();
		List<String[]> tmp = new ArrayList<String[]>();
		for (int i = 0; i < len; i++) {
			String[] row = content.get(i);
			if (row.length != 0) {
				tmp.add(row);
			} else {
				list.add(tmp);
				tmp = new ArrayList<String[]>();
			}
		}
		if (!tmp.isEmpty()) {
			list.add(tmp);
		}
		return list;
	}

	/**
	 * 统计指定某天新闻的数量排名 ...title、count
	 * 
	 * @return
	 */
	private List<String[]> statTheDay(List<List<String[]>> content, List<Integer> marked, String theDay,
			int indexOfTitle, int indexOfTime) {
		List<String[]> res = new ArrayList<String[]>();
		int size = content.size();
		for (int i = 0; i < size; i++) {
			List<String[]> cluster = content.get(i);
			int count = 0;
			int csize = cluster.size();
			for (int j = 0; j < csize; j++) {
				String[] row = cluster.get(j);
				if (TimeUtil.getTimeKey(row[indexOfTime], Interval.DAY).equals(theDay)) {
					count++;
				}
			}
			if (count != 0) {
				res.add(new String[] { cluster.get(marked.get(i))[indexOfTitle], String.valueOf(count) });
			}
		}
		Collections.sort(res, new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				return Integer.valueOf(o2[1]) - Integer.valueOf(o1[1]);
			}
		});
		return res;
	}
	
	
	/**
	 * 爬取数据生成摘要
	 * @param attrs 属性列
	 * @param allContent 文本内容  String[]为一条新闻记录 List<String[]>为一个类簇
	 * @return
	 */
	private List<String[]> generateSummary(String[] attrs, List<List<String[]>> allContent,List<Integer> marked,String topicName) {
		List<String[]> summary = new ArrayList<>();
		int titleIndex = AttrUtil.findIndexOfTitle(attrs);
		int urlIndex = AttrUtil.findIndexOfUrl(attrs);
		if(allContent== null || allContent.size() == 0){
			return summary;
		}
		for (int i = 0; i < allContent.size(); i ++){
			List<String[]> content = allContent.get(i);
			String title = null;
			List<String> sentence = null;
			List<Domain> organization = new ArrayList<Domain>();
			Set<String> urlSet =new HashSet<String>();
			// 是否找到了要摘要的文章
			boolean flag = false;
			// 找到可以爬的sentence
			for (String[] str : content) {
				try{
					String url = UrlUtil.getUrl(str[urlIndex]);
					if(url==null)
						continue;
					if (!flag) {
						sentence = crawler.getSummary(str[urlIndex]);
						if (null != sentence) {
							flag = true;
							title = str[titleIndex];
							sentence.add(0, title);
							Summary s = new Summary(sentence);
							s.summary();
							sentence = s.getSummary(null);
						}
					}
					if(!urlSet.contains(url)){
						urlSet.add(url);
						if(Constant.existDomain.get(url) == null){
							logger.error(url+"暂未录入existDomain中！");
						}else{
							organization.add(Constant.existDomain.get(url));
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					logger.error("提取摘要信息出错！");
				}
			}
			Collections.sort(organization);
			//organization.sort(null);
			String str_organization = "（";
			if (organization.size() == 0) {
				str_organization = "（"+topicName+"）";
			} else {
				for (Domain domain : organization) {
					if(domain == null){
						logger.error("域名为空，摘要信息提取出错！");
						continue;
					}
					str_organization += domain.getName()+ "、";
				}
				str_organization = str_organization.substring(0, str_organization.length() - 1) + "）";
			}
			String str_sentence = "";
			if (flag) {
				for (String string : sentence) {
					str_sentence += string + "。";
				}
			} else {
				title = content.get(marked.get(i))[titleIndex];
				str_sentence = "由于该类新闻没有权威性较高的网站来获取新闻内容，故无法获得摘要。该类新闻的最早报道来源于：" + content.get(marked.get(i))[urlIndex];
			}
			summary.add(new String[] { title, str_sentence, str_organization });
		}
		return summary;
	}
}

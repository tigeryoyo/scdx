package com.hust.scdx.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.hust.scdx.constant.Constant;
import com.hust.scdx.constant.Config.DIRECTORY;
import com.hust.scdx.constant.Constant.KEY;
import com.hust.scdx.constant.Constant.StdfileMap;
import com.hust.scdx.dao.StdfileDao;
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

@Service
public class StdfileServiceImpl implements StdfileService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StdfileServiceImpl.class);

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
	 * 根据stdfileId得到标准文件与摘要
	 */
	@Override
	public Map<String, Object> getStdfileById(String stdfileId) {
		Stdfile stdfile = stdfileDao.queryStdfileById(stdfileId);
		String stdfilePath = DIRECTORY.STDFILE + ConvertUtil.convertDateToPath(stdfile.getUploadTime()) + stdfileId;
		Map<String, Object> stdfileMap = FileUtil.getStdfileExcelcontent(stdfilePath);
		stdfileMap.put(StdfileMap.NAME, stdfile.getStdfileName());
		Map<String, TreeMap<String, Integer>> statMap = AttrUtil
				.statistics((List<String[]>) stdfileMap.get(StdfileMap.CONTENT), Constant.existDomain);
		stdfileMap.put("stat", statMap);
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

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(df, false);
		binder.registerCustomEditor(Date.class, editor);
	}

}

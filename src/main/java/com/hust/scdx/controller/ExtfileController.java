package com.hust.scdx.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.constant.Constant.Cluster;
import com.hust.scdx.model.Extfile;
import com.hust.scdx.model.params.ExtfileQueryCondition;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.util.AttrUtil;
import com.hust.scdx.util.ExcelUtil;
import com.hust.scdx.util.ResultUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/extfile")
/**
 * 基础数据Controller
 * 
 * @author tigerto
 *
 */
public class ExtfileController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExtfileController.class);

	@Autowired
	private ExtfileService extfileService;

	/**
	 * 上传原始文件，经过去重成为基础数据文件存储在文件系统中。
	 * 
	 * @param origfile
	 *            原始文件
	 * @param topicId
	 *            话题id
	 * @param sourceType
	 *            文件类型：新闻or微博
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Object upload(@RequestParam(value = "origfile", required = true) MultipartFile origfile,
			@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "sourceType", required = false) String sourceType, HttpServletRequest request) {
		if (origfile.isEmpty()) {
			logger.info("文件为空。");
			return ResultUtil.errorWithMsg("文件为空。");
		}
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setFile(origfile);
		con.setSourceType(sourceType);
		con.setTopicId(topicId);
		if (extfileService.insert(con, request) == 0) {
			logger.info("文件上传失败。");
			return ResultUtil.errorWithMsg("上传失败。");
		}
		return ResultUtil.success("上传成功。");
	}

	/**
	 * 读取上传文件的第一行（属性行），检查是否满足存在 时间、标题、链接
	 * 
	 * @param origfile
	 *            上传的原始文件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkExtfile")
	public Object checkExtfile(@RequestParam(value = "origfile", required = true) MultipartFile origfile) {
		if (origfile.isEmpty()) {
			return ResultUtil.errorWithMsg("文件是空的");
		}
		try {
			String[] attrs = ExcelUtil.readOrigfileAttrs(origfile.getOriginalFilename(), origfile.getInputStream());
			if (AttrUtil.findIndexOfTitle(attrs) != -1 && AttrUtil.findIndexOfUrl(attrs) != -1
					&& AttrUtil.findIndexOfTime(attrs) != -1) {
				return ResultUtil.successWithoutMsg();
			}
		} catch (Exception e) {
			logger.warn("读取属性行失败。" + e.toString());
		}
		return ResultUtil.errorWithMsg("获取文件属性失败");
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
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryExtfilesByTimeRange")
	public Object queryExtfilesByTimeRange(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "timeRangeType", required = true) String timeRangeType,
			@RequestParam(value = "startTime", required = true) Date startTime,
			@RequestParam(value = "endTime", required = true) Date endTime, HttpServletRequest request) {
		switch (timeRangeType) {
		case "1":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-1*60*1000);
			break;
		case "2":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-1*24*60*60*1000);
			break;
		case "3":
			endTime = new Date();
			startTime = new Date(endTime.getTime()-7*24*60*60*1000);
			break;
		default:
			break;
		}
		System.out.println(startTime.toString());
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		List<Extfile> list = extfileService.queryExtfilesByTimeRange(con);
		if (list == null) {
			return ResultUtil.unknowError();
		}
		return ResultUtil.success(list);
	}

	/**
	 * 根据时间范围聚类。
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
	@ResponseBody
	@RequestMapping("/miningByTimeRange")
	public Object miningByTimeRange(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "startTime", required = true) Date startTime,
			@RequestParam(value = "endTime", required = true) Date endTime, HttpServletRequest request) {
		ExtfileQueryCondition con = new ExtfileQueryCondition();
		con.setTopicId(topicId);
		con.setStartTime(startTime);
		con.setEndTime(endTime);
		// title、url、time、amount
		List<String[]> list = extfileService.miningByTimeRange(con, request);
		if (list == null) {
			return ResultUtil.errorWithMsg("聚类出现bug。");
		}
		JSONObject json = new JSONObject();
		json.put(Cluster.DISPLAYRESULT, list);
		json.put(Cluster.RESULTID, request.getSession().getAttribute(Cluster.RESULTID));
		return ResultUtil.success(json);
	}

	/**
	 * 根据时间范围聚类。
	 * 
	 * @param stdfileIds
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/miningByExtfileIds")
	public Object miningByExtfileIds(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "extfileIds", required = true) List<String> extfileIds, HttpServletRequest request) {
		// title、url、time、amount
		List<String[]> list = extfileService.miningByExtfileIds(topicId, extfileIds, request);
		if (list == null) {
			return ResultUtil.errorWithMsg("聚类出现bug。");
		}
		JSONObject json = new JSONObject();
		json.put(Cluster.DISPLAYRESULT, list);
		json.put(Cluster.RESULTID, request.getSession().getAttribute(Cluster.RESULTID));
		return ResultUtil.success(json);
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(df, false);
		binder.registerCustomEditor(Date.class, editor);
	}
}

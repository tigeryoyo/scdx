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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Result;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/result")
public class ResultController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResultController.class);

	@Autowired
	private ResultService resultService;

	/**
	 * 根据resultId查找结果，返回前台显示的list。
	 * 
	 * @param resultId
	 * @param request
	 * @return displayResult（title、time、url、amount）
	 */
	@ResponseBody
	@RequestMapping("/getDisplayResultById")
	public Object getDisplayResultById(@RequestParam(value = "resultId", required = true) String resultId, HttpServletRequest request) {
		List<String[]> list = resultService.getDisplayResultById(resultId, request);
		if (list == null) {
			logger.error("查找操作结果失败。");
			return ResultUtil.errorWithMsg("查找操作结果失败。");
		}
		return ResultUtil.success(list);
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
	 * @param request
	 * @return 结果记录对象list
	 */
	@ResponseBody
	@RequestMapping("/queryResultByTimeRange")
	public Object queryResultByTimeRange(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "startTime", required = true) Date startTime, @RequestParam(value = "endTime", required = true) Date endTime,
			HttpServletRequest request) {
		List<Result> list = resultService.queryResultByTimeRange(topicId, startTime, endTime);
		if (list == null) {
			logger.error("查找历史操作结果失败。");
			return ResultUtil.errorWithMsg("查找历史操作结果失败。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 重置结果，撤销对结果的二次操作。
	 * 
	 * @param resultId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/resetResultById")
	public Object resetResultById(@RequestParam(value = "resultId", required = true) String resultId, HttpServletRequest request) {
		List<String[]> list = resultService.resetResultById(resultId, request);
		if (list == null) {
			logger.error("查找操作结果失败。");
			return ResultUtil.errorWithMsg("查找操作结果失败。");
		}
		return ResultUtil.success(list);
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(df, false);
		binder.registerCustomEditor(Date.class, editor);
	}
}

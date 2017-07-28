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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.TopicQueryCondition;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.ResultUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/topic")
public class TopicController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

	@Autowired
	private TopicService topicService;
	@Autowired
	private UserService userService;

	/**
	 * 创建专题
	 * 
	 * @param topicName
	 *            专题名
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object createTopic(@RequestParam(value = "topicName", required = true) String topicName, HttpServletRequest request) {
		if (topicService.createTopic(topicName, request) <= 0) {
			logger.info("创建 [" + topicName + "] 专题失败。");
			return ResultUtil.errorWithMsg("创建 [" + topicName + "] 专题失败。");
		}
		return ResultUtil.success("创建 [" + topicName + "] 专题成功。");
	}

	/**
	 * 查询自有的所有专题
	 * 
	 * @param con
	 *            查询条件
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryOwnTopic")
	public Object queryOwnTopic(@RequestBody TopicQueryCondition con, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		con.setCreater(user.getUserName());
		List<Topic> list = topicService.queryTopic(con);
		if (null == list || 0 == list.size()) {
			return ResultUtil.errorWithMsg("没有专题被创建！");
		}
		long count = list.size();
		JSONObject result = new JSONObject();
		long pageTotal = count % 10 == 0 ? (count / 10) : (count / 10 + 1);
		result.put("pageTotal", pageTotal);
		result.put("list", list);
		return ResultUtil.success(result);
	}

	@ResponseBody
	@RequestMapping("/queryOwnTopicCount")
	public Object queryOwnTopicCount(@RequestBody TopicQueryCondition con, HttpServletRequest request) {
		long count = topicService.queryTopicCount(con);
		if (count <= 0) {
			return ResultUtil.errorWithMsg("没有专题被创建。");
		}
		return ResultUtil.success(count);
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
			@RequestParam(value = "startTime", required = true) Date startTime, @RequestParam(value = "endTime", required = true) Date endTime,
			HttpServletRequest request) {
		// title、url、time、amount
		List<String[]> list = topicService.miningByTimeRange(topicId, startTime, endTime, request);
		if (list == null) {
			return ResultUtil.unknowError();
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

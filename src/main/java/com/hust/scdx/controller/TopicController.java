package com.hust.scdx.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Attr;
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
			logger.info("创建「" + topicName + "」专题失败。");
			return ResultUtil.errorWithMsg("创建「" + topicName + "」专题失败。");
		}
		return ResultUtil.success("创建「" + topicName + "」专题成功。");
	}

	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Object deleteTopic(@RequestParam(value = "topicId", required = true) String topicId, HttpServletRequest request) {
		Topic topic = topicService.queryTopicById(topicId);
		if (topic == null) {
			logger.info("「" + topicId + "」专题不存在。");
			return ResultUtil.errorWithMsg("该专题已被删除。");
		}
		String topicName = topic.getTopicName();
		if (topicService.deleteTopicById(topicId) < 0) {
			logger.info("删除「" + topicName + "」专题失败。");
			return ResultUtil.errorWithMsg("删除「" + topicName + "」专题失败。");
		}
		return ResultUtil.success("删除「" + topicName + "」专题成功。");
	}

	@ResponseBody
	@RequestMapping(value = "/setTopicAttr", method = RequestMethod.POST)
	public Object setTopicAttr(@RequestParam(value = "topicId", required = true) String topicId,@RequestParam(value = "attrIds", required = false) String attrIds, HttpServletRequest request) {
		Topic topic = topicService.queryTopicById(topicId);
		if (topic == null) {
			logger.info("「" + topicId + "」专题不存在。");
			return ResultUtil.errorWithMsg("该专题已被删除。");
		}
		if(null == attrIds){
			if (!topicService.setTopicAttr(topicId)) {
				logger.info("设置专题属性失败。");
				return ResultUtil.errorWithMsg("设置专题属性失败。");
			}
			return ResultUtil.success("设置属性成功。");
		}else{
			topic.setAttr(attrIds);
			if (topicService.updateTopicInfo(topic)<=0) {
				logger.info("设置专题属性失败。");
				return ResultUtil.errorWithMsg("设置专题属性失败。");
			}
			return ResultUtil.success("设置属性成功。");
		}
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
		User user = userService.selectCurrentUser(request);
		con.setCreater(user.getUserName());
		long count = topicService.queryTopicCount(con);
		if (count <= 0) {
			return ResultUtil.errorWithMsg("没有专题被创建。");
		}
		return ResultUtil.success(count);
	}

	/**
	 * 查询所有专题
	 * 
	 * @param con
	 *            查询条件
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllTopic")
	public Object queryAllTopic(@RequestBody TopicQueryCondition con, HttpServletRequest request) {
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
	/**
	 * 用于专题属性管理页面的查询
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAllTopic")
	public Object getAllTopic( HttpServletRequest request) {
		List<Topic> list = topicService.queryTopic(null);
		if (null == list || 0 == list.size()) {
			return ResultUtil.errorWithMsg("没有专题被创建！");
		}
		return ResultUtil.success(list);
	}
	
	@ResponseBody
	@RequestMapping("/queryAllTopicCount")
	public Object queryAllTopicCount(@RequestBody TopicQueryCondition con, HttpServletRequest request) {
		long count = topicService.queryTopicCount(con);
		if (count <= 0) {
			return ResultUtil.errorWithMsg("没有专题被创建。");
		}
		return ResultUtil.success(count);
	}
	
	@ResponseBody
	@RequestMapping("/queryTopicAttr")
	public Object queryTopicAttr(@RequestParam(value = "topicId", required = true) String topicId, HttpServletRequest request) {
		System.out.println(topicId);
		List<Attr> attr = topicService.getAttrsByTopicId(topicId);
		if (null == attr || attr.isEmpty()) {
			return ResultUtil.errorWithMsg("查询该专题的自定义属性列失败！");
		}
		return ResultUtil.success(attr);
	}

}

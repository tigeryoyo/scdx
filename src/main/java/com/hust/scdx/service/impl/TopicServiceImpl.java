package com.hust.scdx.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.TopicDao;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.TopicQueryCondition;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.service.StdfileService;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;

@Service
public class TopicServiceImpl implements TopicService {
	@Autowired
	TopicDao topicDao;
	@Autowired
	UserService userService;
	@Autowired
	ExtfileService extfileService;
	@Autowired
	StdfileService stdfileService;
	@Autowired
	ResultService resultService;

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

	/**
	 * 根据topicId删除专题，并删除专题内所有数据
	 */
	@Override
	public int deleteTopicById(String topicId) {
		if (-1 != extfileService.deleteExtfileByTopicId(topicId) && -1 != resultService.deleteResultByTopicId(topicId)
				&& -1 != stdfileService.deleteStdfileByTopicId(topicId)) {
			return topicDao.deleteTopicById(topicId);
		}
		return -1;
	}

	@Override
	public int updateTopicInfo(Topic topic) {
		return topicDao.updateTopicInfo(topic);
	}

	@Override
	public Topic queryTopicById(String topicId) {
		return topicDao.queryTopicById(topicId);
	}

	@Override
	public List<Topic> queryTopicByName(TopicQueryCondition con) {
		return topicDao.queryTopicByName(con);
	}

	@Override
	public long queryTopicCount(TopicQueryCondition con) {
		return topicDao.queryTopicCount(con);
	}

	@Override
	public List<Topic> queryTopic(TopicQueryCondition con) {
		return topicDao.queryTopic(con);
	}
}

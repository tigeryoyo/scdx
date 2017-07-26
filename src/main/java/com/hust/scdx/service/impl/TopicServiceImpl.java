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
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;

@Service
public class TopicServiceImpl implements TopicService {

	@Autowired
	UserService userService;

	@Autowired
	TopicDao topicDao;

	/**
	 * 创建专题
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
	public List<Topic> queryTopic(TopicQueryCondition con) {
		List<Topic> list = topicDao.queryTopicByTopicName(con);
		return list;
	}

	@Override
	public long queryTopicCount(TopicQueryCondition con) {
		return topicDao.queryTopicCount(con);
	}

}

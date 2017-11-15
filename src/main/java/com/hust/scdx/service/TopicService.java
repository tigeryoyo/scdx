package com.hust.scdx.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Topic;
import com.hust.scdx.model.params.TopicQueryCondition;

public interface TopicService {
	int createTopic(String topicName, HttpServletRequest request);

	int deleteTopicById(String topicId);

	int updateTopicInfo(Topic topic);

	Topic queryTopicById(String topicId);
	
	List<Topic> queryTopicByName(TopicQueryCondition con);

	long queryTopicCount(TopicQueryCondition con);
	
	List<Topic> queryTopic(TopicQueryCondition con);

}

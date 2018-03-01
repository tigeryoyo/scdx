package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.TopicDao;
import com.hust.scdx.model.Attr;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.User;
import com.hust.scdx.model.params.TopicQueryCondition;
import com.hust.scdx.service.AttrService;
import com.hust.scdx.service.ExtfileService;
import com.hust.scdx.service.ResultService;
import com.hust.scdx.service.StdfileService;
import com.hust.scdx.service.TopicService;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.AttrUtil;

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
	AttrService attrService;
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
		topic.setAttr(Constant.topicAttrOrder);
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

	@Override
	public String queryAttrByTopicId(String topicId) {
		return topicDao.queryAttrByTopicId(topicId);
	}

	@Override
	public boolean setTopicAttr(String topicId) {
		try {
			List<String> attrs_mainName = new ArrayList<String>();
			List<String> attrs_alias = new ArrayList<String>();
			AttrUtil attrUtil = AttrUtil.getSingleton();
			String attrs = queryAttrByTopicId(topicId);
			if (attrs == null || attrs.isEmpty()) {
				attrs = Constant.topicAttrOrder;
			}
			String[] attrIds = attrs.split(";");
			for (String attrId : attrIds) {
				Attr attr = attrService.queryAttrById(Integer.valueOf(attrId));
				if (attr != null) {
					attrs_mainName.add(attr.getAttrMainname());
					attrs_alias.add(attr.getAttrAlias());
					if (attr.getAttrId() == 1) {
						attrUtil.setTitle_mainName(attr.getAttrMainname());
						attrUtil.setTitle_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 2) {
						attrUtil.setUrl_mainName(attr.getAttrMainname());
						attrUtil.setUrl_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 3) {
						attrUtil.setTime_mainName(attr.getAttrMainname());
						attrUtil.setTime_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 4) {
						attrUtil.setPosting_mainName(attr.getAttrMainname());
						attrUtil.setPosting_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 5) {
						attrUtil.setWebname_mainName(attr.getAttrMainname());
						attrUtil.setWebname_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 6) {
						attrUtil.setType_mainName(attr.getAttrMainname());
						attrUtil.setType_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 7) {
						attrUtil.setColumn_mainName(attr.getAttrMainname());
						attrUtil.setColumn_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 8) {
						attrUtil.setRank_mainName(attr.getAttrMainname());
						attrUtil.setRank_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 9) {
						attrUtil.setWeight_mainName(attr.getAttrMainname());
						attrUtil.setWeight_alias(attr.getAttrAlias());
					} else if (attr.getAttrId() == 10) {
						attrUtil.setIncidence_mainName(attr.getAttrMainname());
						attrUtil.setIncidence_alias(attr.getAttrAlias());
					}
				}
			}
			attrUtil.setAttrs_mainName(attrs_mainName);
			attrUtil.setAttrs_alias(attrs_alias);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}

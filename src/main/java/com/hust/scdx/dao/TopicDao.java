package com.hust.scdx.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.TopicMapper;
import com.hust.scdx.model.Topic;
import com.hust.scdx.model.TopicExample;
import com.hust.scdx.model.TopicExample.Criteria;
import com.hust.scdx.model.params.TopicQueryCondition;

@Repository
public class TopicDao {
	@Autowired
	private TopicMapper topicMapper;

	/**
	 * 新增专题
	 */
	public int insert(Topic topic) {
		return topicMapper.insert(topic);
	}

	/**
	 * 根据专题名查找所有符合条件专题
	 * 
	 * @param con
	 * @return
	 */
	public List<Topic> queryTopicByName(TopicQueryCondition con) {
		TopicExample example = new TopicExample();
		Criteria criteria = example.createCriteria();
		criteria.andCreatorEqualTo(con.getCreater());
		if (!StringUtils.isBlank(con.getTopicName())) {
			// criteria.andTopicNameEqualTo(con.getTopicName());
			criteria.andTopicNameLike("%" + con.getTopicName() + "%");
		}
		example.setOrderByClause("last_update_time desc");
		example.setStart((con.getPageNo() - 1) * con.getPageSize());
		example.setLimit(con.getPageSize());
		return topicMapper.selectByExample(example);
	}

	/**
	 * 根据专题id查询专题
	 * 
	 * @param topicId
	 * @return
	 */
	public Topic queryTopicById(String topicId) {
		return topicMapper.selectByPrimaryKey(topicId);
	}

	/**
	 * 根据topicId查找该attr
	 * @param topicId
	 * @return
	 */
	public String queryAttrByTopicId(String topicId) {
		return queryTopicById(topicId).getAttr();
	}

	/**
	 * 根据专题id删除专题.
	 * 
	 * @param topicId
	 * @return
	 */
	public int deleteTopicById(String topicId) {
		return topicMapper.deleteByPrimaryKey(topicId);
	}

	/**
	 * 根据其他条件查找所有符合条件专题
	 * 
	 * @param con
	 * @return
	 */
	public List<Topic> queryTopic(TopicQueryCondition con) {
		TopicExample example = new TopicExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(con.getTopicId())) {
			criteria.andTopicIdEqualTo(con.getTopicId());
		}
		if (!StringUtils.isBlank(con.getCreater())) {
			criteria.andCreatorEqualTo(con.getCreater());
		}
		if (!StringUtils.isBlank(con.getTopicName())) {
			// criteria.andTopicNameEqualTo(con.getTopicName());
			criteria.andTopicNameLike("%" + con.getTopicName() + "%");
		}
		if (!StringUtils.isBlank(con.getTopicType())) {
			criteria.andTopicTypeEqualTo(con.getTopicType());
		}
		if (null != con.getCreateStartTime()) {
			criteria.andCreateTimeGreaterThanOrEqualTo(con.getCreateStartTime());
		}
		if (null != con.getCreateEndTime()) {
			criteria.andCreateTimeLessThanOrEqualTo(con.getCreateEndTime());
		}
		if (null != con.getLastUpdateStartTime()) {
			criteria.andLastUpdateTimeGreaterThanOrEqualTo(con.getLastUpdateStartTime());
		}
		if (null != con.getLastUpdateEndTime()) {
			criteria.andLastUpdateTimeLessThanOrEqualTo(con.getLastUpdateEndTime());
		}
		example.setOrderByClause("last_update_time desc");
		example.setStart((con.getPageNo() - 1) * con.getPageSize());
		example.setLimit(con.getPageSize());
		return topicMapper.selectByExample(example);
	}

	public long queryTopicCount(TopicQueryCondition con) {
		TopicExample example = new TopicExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(con.getTopicId())) {
			criteria.andTopicIdEqualTo(con.getTopicId());
		}
		if (!StringUtils.isBlank(con.getCreater())) {
			criteria.andCreatorEqualTo(con.getCreater());
		}
		if (!StringUtils.isBlank(con.getTopicName())) {
			criteria.andTopicNameLike("%" + con.getTopicName() + "%");
		}
		if (!StringUtils.isBlank(con.getTopicType())) {
			criteria.andTopicTypeEqualTo(con.getTopicType());
		}
		if (null != con.getCreateStartTime()) {
			criteria.andCreateTimeGreaterThanOrEqualTo(con.getCreateStartTime());
		}
		if (null != con.getCreateEndTime()) {
			criteria.andCreateTimeLessThanOrEqualTo(con.getCreateEndTime());
		}
		if (null != con.getLastUpdateStartTime()) {
			criteria.andLastUpdateTimeGreaterThanOrEqualTo(con.getLastUpdateStartTime());
		}
		if (null != con.getLastUpdateEndTime()) {
			criteria.andLastUpdateTimeLessThanOrEqualTo(con.getLastUpdateEndTime());
		}
		return topicMapper.countByExample(example);
	}

	public int updateTopicInfo(Topic topic) {
		TopicExample example = new TopicExample();
		example.createCriteria().andTopicIdEqualTo(topic.getTopicId());
		return topicMapper.updateByExampleSelective(topic, example);
	}

}

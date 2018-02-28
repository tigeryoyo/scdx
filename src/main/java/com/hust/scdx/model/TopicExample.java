package com.hust.scdx.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopicExample {
	protected String orderByClause;

	protected boolean distinct;

	protected List<Criteria> oredCriteria;

	private int limit;

	private int start;

	public TopicExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andTopicIdIsNull() {
			addCriterion("topic_id is null");
			return (Criteria) this;
		}

		public Criteria andTopicIdIsNotNull() {
			addCriterion("topic_id is not null");
			return (Criteria) this;
		}

		public Criteria andTopicIdEqualTo(String value) {
			addCriterion("topic_id =", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdNotEqualTo(String value) {
			addCriterion("topic_id <>", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdGreaterThan(String value) {
			addCriterion("topic_id >", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdGreaterThanOrEqualTo(String value) {
			addCriterion("topic_id >=", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdLessThan(String value) {
			addCriterion("topic_id <", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdLessThanOrEqualTo(String value) {
			addCriterion("topic_id <=", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdLike(String value) {
			addCriterion("topic_id like", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdNotLike(String value) {
			addCriterion("topic_id not like", value, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdIn(List<String> values) {
			addCriterion("topic_id in", values, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdNotIn(List<String> values) {
			addCriterion("topic_id not in", values, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdBetween(String value1, String value2) {
			addCriterion("topic_id between", value1, value2, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicIdNotBetween(String value1, String value2) {
			addCriterion("topic_id not between", value1, value2, "topicId");
			return (Criteria) this;
		}

		public Criteria andTopicNameIsNull() {
			addCriterion("topic_name is null");
			return (Criteria) this;
		}

		public Criteria andTopicNameIsNotNull() {
			addCriterion("topic_name is not null");
			return (Criteria) this;
		}

		public Criteria andTopicNameEqualTo(String value) {
			addCriterion("topic_name =", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameNotEqualTo(String value) {
			addCriterion("topic_name <>", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameGreaterThan(String value) {
			addCriterion("topic_name >", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameGreaterThanOrEqualTo(String value) {
			addCriterion("topic_name >=", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameLessThan(String value) {
			addCriterion("topic_name <", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameLessThanOrEqualTo(String value) {
			addCriterion("topic_name <=", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameLike(String value) {
			addCriterion("topic_name like", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameNotLike(String value) {
			addCriterion("topic_name not like", value, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameIn(List<String> values) {
			addCriterion("topic_name in", values, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameNotIn(List<String> values) {
			addCriterion("topic_name not in", values, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameBetween(String value1, String value2) {
			addCriterion("topic_name between", value1, value2, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicNameNotBetween(String value1, String value2) {
			addCriterion("topic_name not between", value1, value2, "topicName");
			return (Criteria) this;
		}

		public Criteria andTopicTypeIsNull() {
			addCriterion("topic_type is null");
			return (Criteria) this;
		}

		public Criteria andTopicTypeIsNotNull() {
			addCriterion("topic_type is not null");
			return (Criteria) this;
		}

		public Criteria andTopicTypeEqualTo(String value) {
			addCriterion("topic_type =", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeNotEqualTo(String value) {
			addCriterion("topic_type <>", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeGreaterThan(String value) {
			addCriterion("topic_type >", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeGreaterThanOrEqualTo(String value) {
			addCriterion("topic_type >=", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeLessThan(String value) {
			addCriterion("topic_type <", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeLessThanOrEqualTo(String value) {
			addCriterion("topic_type <=", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeLike(String value) {
			addCriterion("topic_type like", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeNotLike(String value) {
			addCriterion("topic_type not like", value, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeIn(List<String> values) {
			addCriterion("topic_type in", values, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeNotIn(List<String> values) {
			addCriterion("topic_type not in", values, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeBetween(String value1, String value2) {
			addCriterion("topic_type between", value1, value2, "topicType");
			return (Criteria) this;
		}

		public Criteria andTopicTypeNotBetween(String value1, String value2) {
			addCriterion("topic_type not between", value1, value2, "topicType");
			return (Criteria) this;
		}

		public Criteria andAttrIsNull() {
			addCriterion("attr is null");
			return (Criteria) this;
		}

		public Criteria andAttrIsNotNull() {
			addCriterion("attr is not null");
			return (Criteria) this;
		}

		public Criteria andAttrEqualTo(String value) {
			addCriterion("attr =", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrNotEqualTo(String value) {
			addCriterion("attr <>", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrGreaterThan(String value) {
			addCriterion("attr >", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrGreaterThanOrEqualTo(String value) {
			addCriterion("attr >=", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrLessThan(String value) {
			addCriterion("attr <", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrLessThanOrEqualTo(String value) {
			addCriterion("attr <=", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrLike(String value) {
			addCriterion("attr like", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrNotLike(String value) {
			addCriterion("attr not like", value, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrIn(List<String> values) {
			addCriterion("attr in", values, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrNotIn(List<String> values) {
			addCriterion("attr not in", values, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrBetween(String value1, String value2) {
			addCriterion("attr between", value1, value2, "attr");
			return (Criteria) this;
		}

		public Criteria andAttrNotBetween(String value1, String value2) {
			addCriterion("attr not between", value1, value2, "attr");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIsNull() {
			addCriterion("create_time is null");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIsNotNull() {
			addCriterion("create_time is not null");
			return (Criteria) this;
		}

		public Criteria andCreateTimeEqualTo(Date value) {
			addCriterion("create_time =", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotEqualTo(Date value) {
			addCriterion("create_time <>", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeGreaterThan(Date value) {
			addCriterion("create_time >", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("create_time >=", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeLessThan(Date value) {
			addCriterion("create_time <", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
			addCriterion("create_time <=", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIn(List<Date> values) {
			addCriterion("create_time in", values, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotIn(List<Date> values) {
			addCriterion("create_time not in", values, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeBetween(Date value1, Date value2) {
			addCriterion("create_time between", value1, value2, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
			addCriterion("create_time not between", value1, value2, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreatorIsNull() {
			addCriterion("creator is null");
			return (Criteria) this;
		}

		public Criteria andCreatorIsNotNull() {
			addCriterion("creator is not null");
			return (Criteria) this;
		}

		public Criteria andCreatorEqualTo(String value) {
			addCriterion("creator =", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorNotEqualTo(String value) {
			addCriterion("creator <>", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorGreaterThan(String value) {
			addCriterion("creator >", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorGreaterThanOrEqualTo(String value) {
			addCriterion("creator >=", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorLessThan(String value) {
			addCriterion("creator <", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorLessThanOrEqualTo(String value) {
			addCriterion("creator <=", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorLike(String value) {
			addCriterion("creator like", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorNotLike(String value) {
			addCriterion("creator not like", value, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorIn(List<String> values) {
			addCriterion("creator in", values, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorNotIn(List<String> values) {
			addCriterion("creator not in", values, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorBetween(String value1, String value2) {
			addCriterion("creator between", value1, value2, "creator");
			return (Criteria) this;
		}

		public Criteria andCreatorNotBetween(String value1, String value2) {
			addCriterion("creator not between", value1, value2, "creator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorIsNull() {
			addCriterion("last_operator is null");
			return (Criteria) this;
		}

		public Criteria andLastOperatorIsNotNull() {
			addCriterion("last_operator is not null");
			return (Criteria) this;
		}

		public Criteria andLastOperatorEqualTo(String value) {
			addCriterion("last_operator =", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorNotEqualTo(String value) {
			addCriterion("last_operator <>", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorGreaterThan(String value) {
			addCriterion("last_operator >", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorGreaterThanOrEqualTo(String value) {
			addCriterion("last_operator >=", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorLessThan(String value) {
			addCriterion("last_operator <", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorLessThanOrEqualTo(String value) {
			addCriterion("last_operator <=", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorLike(String value) {
			addCriterion("last_operator like", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorNotLike(String value) {
			addCriterion("last_operator not like", value, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorIn(List<String> values) {
			addCriterion("last_operator in", values, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorNotIn(List<String> values) {
			addCriterion("last_operator not in", values, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorBetween(String value1, String value2) {
			addCriterion("last_operator between", value1, value2, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastOperatorNotBetween(String value1, String value2) {
			addCriterion("last_operator not between", value1, value2, "lastOperator");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeIsNull() {
			addCriterion("last_update_time is null");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeIsNotNull() {
			addCriterion("last_update_time is not null");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeEqualTo(Date value) {
			addCriterion("last_update_time =", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeNotEqualTo(Date value) {
			addCriterion("last_update_time <>", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeGreaterThan(Date value) {
			addCriterion("last_update_time >", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("last_update_time >=", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeLessThan(Date value) {
			addCriterion("last_update_time <", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeLessThanOrEqualTo(Date value) {
			addCriterion("last_update_time <=", value, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeIn(List<Date> values) {
			addCriterion("last_update_time in", values, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeNotIn(List<Date> values) {
			addCriterion("last_update_time not in", values, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeBetween(Date value1, Date value2) {
			addCriterion("last_update_time between", value1, value2, "lastUpdateTime");
			return (Criteria) this;
		}

		public Criteria andLastUpdateTimeNotBetween(Date value1, Date value2) {
			addCriterion("last_update_time not between", value1, value2, "lastUpdateTime");
			return (Criteria) this;
		}
	}

	public static class Criteria extends GeneratedCriteria {

		protected Criteria() {
			super();
		}
	}

	public static class Criterion {
		private String condition;

		private Object value;

		private Object secondValue;

		private boolean noValue;

		private boolean singleValue;

		private boolean betweenValue;

		private boolean listValue;

		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion() {
			super();
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}
}
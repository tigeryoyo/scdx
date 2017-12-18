package com.hust.scdx.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StdfileExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StdfileExample() {
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

        public Criteria andStdfileIdIsNull() {
            addCriterion("stdfile_id is null");
            return (Criteria) this;
        }

        public Criteria andStdfileIdIsNotNull() {
            addCriterion("stdfile_id is not null");
            return (Criteria) this;
        }

        public Criteria andStdfileIdEqualTo(String value) {
            addCriterion("stdfile_id =", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdNotEqualTo(String value) {
            addCriterion("stdfile_id <>", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdGreaterThan(String value) {
            addCriterion("stdfile_id >", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdGreaterThanOrEqualTo(String value) {
            addCriterion("stdfile_id >=", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdLessThan(String value) {
            addCriterion("stdfile_id <", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdLessThanOrEqualTo(String value) {
            addCriterion("stdfile_id <=", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdLike(String value) {
            addCriterion("stdfile_id like", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdNotLike(String value) {
            addCriterion("stdfile_id not like", value, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdIn(List<String> values) {
            addCriterion("stdfile_id in", values, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdNotIn(List<String> values) {
            addCriterion("stdfile_id not in", values, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdBetween(String value1, String value2) {
            addCriterion("stdfile_id between", value1, value2, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileIdNotBetween(String value1, String value2) {
            addCriterion("stdfile_id not between", value1, value2, "stdfileId");
            return (Criteria) this;
        }

        public Criteria andStdfileNameIsNull() {
            addCriterion("stdfile_name is null");
            return (Criteria) this;
        }

        public Criteria andStdfileNameIsNotNull() {
            addCriterion("stdfile_name is not null");
            return (Criteria) this;
        }

        public Criteria andStdfileNameEqualTo(String value) {
            addCriterion("stdfile_name =", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameNotEqualTo(String value) {
            addCriterion("stdfile_name <>", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameGreaterThan(String value) {
            addCriterion("stdfile_name >", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameGreaterThanOrEqualTo(String value) {
            addCriterion("stdfile_name >=", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameLessThan(String value) {
            addCriterion("stdfile_name <", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameLessThanOrEqualTo(String value) {
            addCriterion("stdfile_name <=", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameLike(String value) {
            addCriterion("stdfile_name like", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameNotLike(String value) {
            addCriterion("stdfile_name not like", value, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameIn(List<String> values) {
            addCriterion("stdfile_name in", values, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameNotIn(List<String> values) {
            addCriterion("stdfile_name not in", values, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameBetween(String value1, String value2) {
            addCriterion("stdfile_name between", value1, value2, "stdfileName");
            return (Criteria) this;
        }

        public Criteria andStdfileNameNotBetween(String value1, String value2) {
            addCriterion("stdfile_name not between", value1, value2, "stdfileName");
            return (Criteria) this;
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

        public Criteria andSizeIsNull() {
            addCriterion("size is null");
            return (Criteria) this;
        }

        public Criteria andSizeIsNotNull() {
            addCriterion("size is not null");
            return (Criteria) this;
        }

        public Criteria andSizeEqualTo(Integer value) {
            addCriterion("size =", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotEqualTo(Integer value) {
            addCriterion("size <>", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeGreaterThan(Integer value) {
            addCriterion("size >", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeGreaterThanOrEqualTo(Integer value) {
            addCriterion("size >=", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeLessThan(Integer value) {
            addCriterion("size <", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeLessThanOrEqualTo(Integer value) {
            addCriterion("size <=", value, "size");
            return (Criteria) this;
        }

        public Criteria andSizeIn(List<Integer> values) {
            addCriterion("size in", values, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotIn(List<Integer> values) {
            addCriterion("size not in", values, "size");
            return (Criteria) this;
        }

        public Criteria andSizeBetween(Integer value1, Integer value2) {
            addCriterion("size between", value1, value2, "size");
            return (Criteria) this;
        }

        public Criteria andSizeNotBetween(Integer value1, Integer value2) {
            addCriterion("size not between", value1, value2, "size");
            return (Criteria) this;
        }

        public Criteria andLineNumberIsNull() {
            addCriterion("line_number is null");
            return (Criteria) this;
        }

        public Criteria andLineNumberIsNotNull() {
            addCriterion("line_number is not null");
            return (Criteria) this;
        }

        public Criteria andLineNumberEqualTo(Integer value) {
            addCriterion("line_number =", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberNotEqualTo(Integer value) {
            addCriterion("line_number <>", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberGreaterThan(Integer value) {
            addCriterion("line_number >", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("line_number >=", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberLessThan(Integer value) {
            addCriterion("line_number <", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberLessThanOrEqualTo(Integer value) {
            addCriterion("line_number <=", value, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberIn(List<Integer> values) {
            addCriterion("line_number in", values, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberNotIn(List<Integer> values) {
            addCriterion("line_number not in", values, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberBetween(Integer value1, Integer value2) {
            addCriterion("line_number between", value1, value2, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andLineNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("line_number not between", value1, value2, "lineNumber");
            return (Criteria) this;
        }

        public Criteria andUploadTimeIsNull() {
            addCriterion("upload_time is null");
            return (Criteria) this;
        }

        public Criteria andUploadTimeIsNotNull() {
            addCriterion("upload_time is not null");
            return (Criteria) this;
        }

        public Criteria andUploadTimeEqualTo(Date value) {
            addCriterion("upload_time =", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeNotEqualTo(Date value) {
            addCriterion("upload_time <>", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeGreaterThan(Date value) {
            addCriterion("upload_time >", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("upload_time >=", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeLessThan(Date value) {
            addCriterion("upload_time <", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeLessThanOrEqualTo(Date value) {
            addCriterion("upload_time <=", value, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeIn(List<Date> values) {
            addCriterion("upload_time in", values, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeNotIn(List<Date> values) {
            addCriterion("upload_time not in", values, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeBetween(Date value1, Date value2) {
            addCriterion("upload_time between", value1, value2, "uploadTime");
            return (Criteria) this;
        }

        public Criteria andUploadTimeNotBetween(Date value1, Date value2) {
            addCriterion("upload_time not between", value1, value2, "uploadTime");
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

        public Criteria andDatatimeIsNull() {
            addCriterion("datatime is null");
            return (Criteria) this;
        }

        public Criteria andDatatimeIsNotNull() {
            addCriterion("datatime is not null");
            return (Criteria) this;
        }

        public Criteria andDatatimeEqualTo(String value) {
            addCriterion("datatime =", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeNotEqualTo(String value) {
            addCriterion("datatime <>", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeGreaterThan(String value) {
            addCriterion("datatime >", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeGreaterThanOrEqualTo(String value) {
            addCriterion("datatime >=", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeLessThan(String value) {
            addCriterion("datatime <", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeLessThanOrEqualTo(String value) {
            addCriterion("datatime <=", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeLike(String value) {
            addCriterion("datatime like", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeNotLike(String value) {
            addCriterion("datatime not like", value, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeIn(List<String> values) {
            addCriterion("datatime in", values, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeNotIn(List<String> values) {
            addCriterion("datatime not in", values, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeBetween(String value1, String value2) {
            addCriterion("datatime between", value1, value2, "datatime");
            return (Criteria) this;
        }

        public Criteria andDatatimeNotBetween(String value1, String value2) {
            addCriterion("datatime not between", value1, value2, "datatime");
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
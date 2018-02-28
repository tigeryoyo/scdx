package com.hust.scdx.model;

import java.util.ArrayList;
import java.util.List;

public class AttrExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AttrExample() {
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

        public Criteria andAttrIdIsNull() {
            addCriterion("attr_id is null");
            return (Criteria) this;
        }

        public Criteria andAttrIdIsNotNull() {
            addCriterion("attr_id is not null");
            return (Criteria) this;
        }

        public Criteria andAttrIdEqualTo(Integer value) {
            addCriterion("attr_id =", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotEqualTo(Integer value) {
            addCriterion("attr_id <>", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdGreaterThan(Integer value) {
            addCriterion("attr_id >", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("attr_id >=", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdLessThan(Integer value) {
            addCriterion("attr_id <", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdLessThanOrEqualTo(Integer value) {
            addCriterion("attr_id <=", value, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdIn(List<Integer> values) {
            addCriterion("attr_id in", values, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotIn(List<Integer> values) {
            addCriterion("attr_id not in", values, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdBetween(Integer value1, Integer value2) {
            addCriterion("attr_id between", value1, value2, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrIdNotBetween(Integer value1, Integer value2) {
            addCriterion("attr_id not between", value1, value2, "attrId");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameIsNull() {
            addCriterion("attr_mainname is null");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameIsNotNull() {
            addCriterion("attr_mainname is not null");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameEqualTo(String value) {
            addCriterion("attr_mainname =", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameNotEqualTo(String value) {
            addCriterion("attr_mainname <>", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameGreaterThan(String value) {
            addCriterion("attr_mainname >", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameGreaterThanOrEqualTo(String value) {
            addCriterion("attr_mainname >=", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameLessThan(String value) {
            addCriterion("attr_mainname <", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameLessThanOrEqualTo(String value) {
            addCriterion("attr_mainname <=", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameLike(String value) {
            addCriterion("attr_mainname like", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameNotLike(String value) {
            addCriterion("attr_mainname not like", value, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameIn(List<String> values) {
            addCriterion("attr_mainname in", values, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameNotIn(List<String> values) {
            addCriterion("attr_mainname not in", values, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameBetween(String value1, String value2) {
            addCriterion("attr_mainname between", value1, value2, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrMainnameNotBetween(String value1, String value2) {
            addCriterion("attr_mainname not between", value1, value2, "attrMainname");
            return (Criteria) this;
        }

        public Criteria andAttrAliasIsNull() {
            addCriterion("attr_alias is null");
            return (Criteria) this;
        }

        public Criteria andAttrAliasIsNotNull() {
            addCriterion("attr_alias is not null");
            return (Criteria) this;
        }

        public Criteria andAttrAliasEqualTo(String value) {
            addCriterion("attr_alias =", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasNotEqualTo(String value) {
            addCriterion("attr_alias <>", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasGreaterThan(String value) {
            addCriterion("attr_alias >", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasGreaterThanOrEqualTo(String value) {
            addCriterion("attr_alias >=", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasLessThan(String value) {
            addCriterion("attr_alias <", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasLessThanOrEqualTo(String value) {
            addCriterion("attr_alias <=", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasLike(String value) {
            addCriterion("attr_alias like", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasNotLike(String value) {
            addCriterion("attr_alias not like", value, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasIn(List<String> values) {
            addCriterion("attr_alias in", values, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasNotIn(List<String> values) {
            addCriterion("attr_alias not in", values, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasBetween(String value1, String value2) {
            addCriterion("attr_alias between", value1, value2, "attrAlias");
            return (Criteria) this;
        }

        public Criteria andAttrAliasNotBetween(String value1, String value2) {
            addCriterion("attr_alias not between", value1, value2, "attrAlias");
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
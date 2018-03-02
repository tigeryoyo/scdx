package com.hust.scdx.model.params;

public class AttrQueryCondition {

	private String mainName;
	private String alias;
	private Integer start;
	private Integer limit;


	public String getMainName() {
		return mainName;
	}

	public void setMainName(String mainName) {
		this.mainName = mainName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "AttrQueryCondition [mainName=" + mainName + ", alias=" + alias + ",start=" + start + ", limit=" + limit + "]";
	}

}

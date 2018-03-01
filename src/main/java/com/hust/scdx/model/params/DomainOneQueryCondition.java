package com.hust.scdx.model.params;

import java.util.List;

public class DomainOneQueryCondition {
	private String url;
	private String name;
	private String column;
	private List<String> type;
	private String incidence;
	private List<String> rank;
	private Boolean isFather;
	private Boolean maintenanceStatus;
	private Integer weightStart;
	private Integer weightEnd;
	private Integer timeSorting;
	private Integer urlSorting;
	private Integer nameSorting;
	private Integer columnSorting;
	private Integer typeSorting;
	private Integer rankSorting;
	private Integer weightSorting;
	private Integer maintenanceSorting;
	private Integer start;
	private Integer limit;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isFather() {
		return isFather;
	}

	public void setFather(boolean isFather) {
		this.isFather = isFather;
	}

	public String getIncidence() {
		return incidence;
	}

	public void setIncidence(String incidence) {
		this.incidence = incidence;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getRank() {
		return rank;
	}

	public void setRank(List<String> rank) {
		this.rank = rank;
	}

	public Boolean getIsFather() {
		return isFather;
	}

	public void setIsFather(Boolean isFather) {
		this.isFather = isFather;
	}

	public Boolean getMaintenanceStatus() {
		return maintenanceStatus;
	}

	public void setMaintenanceStatus(Boolean maintenanceStatus) {
		this.maintenanceStatus = maintenanceStatus;
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

	public Integer getWeightStart() {
		return weightStart;
	}

	public void setWeightStart(Integer weightStart) {
		this.weightStart = weightStart;
	}

	public Integer getWeightEnd() {
		return weightEnd;
	}

	public void setWeightEnd(Integer weightEnd) {
		this.weightEnd = weightEnd;
	}

	public Integer getUrlSorting() {
		return urlSorting;
	}

	public void setUrlSorting(Integer urlSorting) {
		this.urlSorting = urlSorting;
	}

	public Integer getNameSorting() {
		return nameSorting;
	}

	public void setNameSorting(Integer nameSorting) {
		this.nameSorting = nameSorting;
	}

	public Integer getColumnSorting() {
		return columnSorting;
	}

	public void setColumnSorting(Integer columnSorting) {
		this.columnSorting = columnSorting;
	}

	public Integer getTypeSorting() {
		return typeSorting;
	}

	public void setTypeSorting(Integer typeSorting) {
		this.typeSorting = typeSorting;
	}

	public Integer getRankSorting() {
		return rankSorting;
	}

	public void setRankSorting(Integer rankSorting) {
		this.rankSorting = rankSorting;
	}

	public Integer getWeightSorting() {
		return weightSorting;
	}

	public void setWeightSorting(Integer weightSorting) {
		this.weightSorting = weightSorting;
	}

	public Integer getTimeSorting() {
		return timeSorting;
	}

	public void setTimeSorting(Integer timeSorting) {
		this.timeSorting = timeSorting;
	}

	public Integer getMaintenanceSorting() {
		return maintenanceSorting;
	}

	public void setMaintenanceSorting(Integer maintenanceSorting) {
		this.maintenanceSorting = maintenanceSorting;
	}
	
}

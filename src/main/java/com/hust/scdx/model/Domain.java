package com.hust.scdx.model;

/**
 * 域名的基本信息
 * @author Jack
 *
 */
public class Domain {
    private String url;

    private String name;

    private String column;

    private String type;

    private String rank;

    private String incidence;

    private Integer weight;
    
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getIncidence() {
		return incidence;
	}

	public void setIncidence(String incidence) {
		this.incidence = incidence;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public void setDomainFormOne(DomainOne one){
		this.url = one.getUrl();
		this.name = one.getName();
		this.type = one.getType();
		this.column = one.getColumn();
		this.rank = one.getRank();
		this.incidence = one.getIncidence();
		this.weight = one.getWeight();
	}
	
	public void setDomainFormTwo(DomainTwo two){
		this.url = two.getUrl();
		this.name = two.getName();
		this.type = two.getType();
		this.column = two.getColumn();
		this.rank = two.getRank();
		this.incidence = two.getIncidence();
		this.weight = two.getWeight();
	}
	
	/**
	 * 根据域名信息生成一级域名对象
	 * 仅包含url,name,type,column,rank,incidence,weight基础属性
	 * uuid、updateTime,isFather属性请手动设置
	 * 必要时请覆盖url属性 
	 * @return
	 */
	public DomainOne getDomainOneBaseProperty(){
		DomainOne one  = new DomainOne();
		one.setUrl(url);
		one.setName(name);
		one.setType(type);
		one.setColumn(column);
		one.setRank(rank);
		one.setWeight(weight);
		one.setIncidence(incidence);
		return one;
	}
	
	/**
	 * 根据域名信息生成二级域名对象
	 * 仅包含url,name,type,column,rank,incidence,weight基础属性
	 * uuid、updateTime、fatherUuid属性请手动设置
	 * 必要时请覆盖url属性 
	 * @return
	 */
	public DomainTwo getDomainTwoBaseProperty(){
		DomainTwo two  = new DomainTwo();
		two.setUrl(url);
		two.setName(name);
		two.setType(type);
		two.setColumn(column);
		two.setRank(rank);
		two.setWeight(weight);
		two.setIncidence(incidence);
		return two;
	}
}

package com.hust.scdx.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DomainStore {
    private String url;

    private String name;

    private String column;

    private String type;

    private String rank;

    private String incidence;

    private Date createTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column == null ? null : column.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank == null ? null : rank.trim();
    }

    public String getIncidence() {
        return incidence;
    }

    public void setIncidence(String incidence) {
        this.incidence = incidence == null ? null : incidence.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public static DomainStore getDomainStoreFromDomain(Domain domain){
    	if(null == domain)return null;
    	DomainStore store = new DomainStore();
    	if(null != domain.getUrl())store.setUrl(domain.getUrl());else return null;
    	if(null != domain.getName())store.setName(domain.getName());
    	if(null != domain.getColumn())store.setColumn(domain.getColumn());
    	if(null != domain.getType())store.setType(domain.getType());
    	if(null != domain.getRank())store.setRank(domain.getRank());
    	if(null != domain.getIncidence())store.setIncidence(domain.getIncidence());
    	store.setCreateTime(new Date());
    	return store;
    }
    
    public static List<DomainStore> getDomainStoreFromDomain(List<Domain> list){
    	if(null==list || list.size()==0)return null;
    	List<DomainStore> res = new ArrayList<DomainStore>();
    	for (Domain domain : list) {
    		DomainStore store = new DomainStore();
        	if(null != domain.getUrl())store.setUrl(domain.getUrl());else continue;
        	if(null != domain.getName())store.setName(domain.getName());
        	if(null != domain.getColumn())store.setColumn(domain.getColumn());
        	if(null != domain.getType())store.setType(domain.getType());
        	if(null != domain.getRank())store.setRank(domain.getRank());
        	if(null != domain.getIncidence())store.setIncidence(domain.getIncidence());
        	store.setCreateTime(new Date());
        	res.add(store);
		}
    	return res;
    }
}
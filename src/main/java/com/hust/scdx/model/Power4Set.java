package com.hust.scdx.model;

import java.io.Serializable;

public class Power4Set implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer powerId;

    private String powerName;

    private String powerUrl;
    
    private boolean owned;
    
    public Power4Set() {
		// TODO Auto-generated constructor stub
    	super();
	}
    /**
     * 初试化power，默认不拥有该权限，即owned值为false
     * @param p
     */
    public Power4Set(Power p){
    	super();
    	this.setPowerId(p.getPowerId());
    	this.setPowerName(p.getPowerName());
    	this.setPowerUrl(p.getPowerUrl());
    	this.setOwned(false);
    }

    public Integer getPowerId() {
        return powerId;
    }

    public void setPowerId(Integer powerId) {
        this.powerId = powerId;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName == null ? null : powerName.trim();
    }

    public String getPowerUrl() {
        return powerUrl;
    }

    public void setPowerUrl(String powerUrl) {
        this.powerUrl = powerUrl == null ? null : powerUrl.trim();
    }     

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public boolean equals(Power4Set obj) {
		// TODO Auto-generated method stub
		return this.getPowerId().equals(obj.getPowerId());
	}

	
}

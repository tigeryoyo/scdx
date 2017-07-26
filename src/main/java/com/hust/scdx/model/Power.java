package com.hust.scdx.model;

import java.io.Serializable;

public class Power implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer powerId;

    private String powerName;

    private String powerUrl;

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
}
package com.example.instructions.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CanonicalTrade {
    @JsonProperty("platform_id")
    private String platformId;
    private Trade trade;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }
}


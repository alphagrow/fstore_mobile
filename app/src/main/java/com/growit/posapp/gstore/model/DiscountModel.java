package com.growit.posapp.gstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiscountModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("loyalty_rewards")
    @Expose
    private List<DiscountListModel> loyaltyRewards;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public List<DiscountListModel> getLoyaltyRewards() {
        return loyaltyRewards;
    }

    public void setLoyaltyRewards(List<DiscountListModel> loyaltyRewards) {
        this.loyaltyRewards = loyaltyRewards;
    }

}

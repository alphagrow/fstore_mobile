package com.growit.posapp.gstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftCardModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statuscode")
    @Expose
    private long statuscode;
    @SerializedName("data")
    @Expose
    private List<GiftCardListModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(long statuscode) {
        this.statuscode = statuscode;
    }

    public List<GiftCardListModel> getData() {
        return data;
    }

    public void setData(List<GiftCardListModel> data) {
        this.data = data;
    }
}

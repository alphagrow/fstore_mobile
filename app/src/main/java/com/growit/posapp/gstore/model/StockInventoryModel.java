package com.growit.posapp.gstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockInventoryModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("data")
    @Expose
    private List<StockInventoryModelList> data;

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

    public List<StockInventoryModelList> getData() {
        return data;
    }

    public void setData(List<StockInventoryModelList> data) {
        this.data = data;
    }
}

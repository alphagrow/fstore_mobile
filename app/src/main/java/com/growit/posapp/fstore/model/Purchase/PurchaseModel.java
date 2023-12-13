package com.growit.posapp.fstore.model.Purchase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("data")
    @Expose
    private List<PurchaseCategoryModel> data;
    @SerializedName("orders")
    @Expose
    private List<PurchaseOrder> orders;

    public List<PurchaseOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<PurchaseOrder> orders) {
        this.orders = orders;
    }

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

    public List<PurchaseCategoryModel> getData() {
        return data;
    }

    public void setData(List<PurchaseCategoryModel> data) {
        this.data = data;
    }
}

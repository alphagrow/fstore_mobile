
package com.growit.posapp.gstore.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDetail {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("data")
    @Expose
    private List<Datum> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProductDetail withStatus(String status) {
        this.status = status;
        return this;
    }


    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public ProductDetail withStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
        return this;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public ProductDetail withData(List<Datum> data) {
        this.data = data;
        return this;
    }


    @SerializedName("orders")
    @Expose
    private List<OrderData> orders;

    public List<OrderData> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderData> orders) {
        this.orders = orders;
    }

    public ProductDetail withOrders(List<OrderData> orders) {
        this.orders = orders;
        return this;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions;

}

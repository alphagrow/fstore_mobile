package com.growit.posapp.gstore.model;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("user_name")
    @Expose
    private String userName;

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @SerializedName("payment_type")
    @Expose
    private String payment_type;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    @SerializedName("amount_paid")
    @Expose
    private String amountPaid;

    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("pos_reference")
    @Expose
    private String posReference;
    @SerializedName("products")
    @Expose
    private List<Product> products;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderData withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderData withOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public OrderData withOrderDate(String orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public OrderData withSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }


    public String getPosReference() {
        return posReference;
    }

    public void setPosReference(String posReference) {
        this.posReference = posReference;
    }

    public OrderData withPosReference(String posReference) {
        this.posReference = posReference;
        return this;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public OrderData withProducts(List<Product> products) {
        this.products = products;
        return this;
    }

}

package com.growit.posapp.gstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("payment_date")
    @Expose
    private String paymentDate;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("pos_order")
    @Expose
    private String posOrder;

    public String getPos_order_id() {
        return pos_order_id;
    }

    public void setPos_order_id(String pos_order_id) {
        this.pos_order_id = pos_order_id;
    }

    @SerializedName("pos_order_id")
    @Expose
    private String pos_order_id;

    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("card_type")
    @Expose
    private String cardType;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @SerializedName("customer_name")
    @Expose
    private String customerName;

    @SerializedName("cardholder_name")
    @Expose
    private String cardholderName;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Transaction withPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Transaction withAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Transaction withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public String getPosOrder() {
        return posOrder;
    }

    public void setPosOrder(String posOrder) {
        this.posOrder = posOrder;
    }

    public Transaction withPosOrder(String posOrder) {
        this.posOrder = posOrder;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Transaction withSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Transaction withCardType(String cardType) {
        this.cardType = cardType;
        return this;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public Transaction withCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Transaction withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

}
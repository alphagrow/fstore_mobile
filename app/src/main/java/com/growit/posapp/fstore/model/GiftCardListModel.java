package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GiftCardListModel {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("expiration_date")
    @Expose
    private String expirationDate;

    public String getCurrent_server_date() {
        return current_server_date;
    }

    public void setCurrent_server_date(String current_server_date) {
        this.current_server_date = current_server_date;
    }

    @SerializedName("current_server_date")
    @Expose
    private String current_server_date;

    @SerializedName("partner_id")
    @Expose
    private boolean partnerId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isPartnerId() {
        return partnerId;
    }

    public void setPartnerId(boolean partnerId) {
        this.partnerId = partnerId;
    }

}

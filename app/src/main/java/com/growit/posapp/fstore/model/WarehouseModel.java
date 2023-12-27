package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WarehouseModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("purchase_journal_id")
    @Expose
    private Object purchaseJournalId;
    @SerializedName("sale_journal_id")
    @Expose
    private Object saleJournalId;
    @SerializedName("partner_name")
    @Expose
    private String partnerName;
    @SerializedName("partner_id")
    @Expose
    private Integer partnerId;

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    @SerializedName("address")
    @Expose
    private AddressModel address;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Object getPurchaseJournalId() {
        return purchaseJournalId;
    }

    public void setPurchaseJournalId(Object purchaseJournalId) {
        this.purchaseJournalId = purchaseJournalId;
    }

    public Object getSaleJournalId() {
        return saleJournalId;
    }

    public void setSaleJournalId(Object saleJournalId) {
        this.saleJournalId = saleJournalId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }
}

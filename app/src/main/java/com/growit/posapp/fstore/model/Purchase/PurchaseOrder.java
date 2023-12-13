package com.growit.posapp.fstore.model.Purchase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseOrder {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("partner_id")
    @Expose
    private String partnerId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("picking_type_id")
    @Expose
    private String pickingTypeId;
    @SerializedName("receipt_status")
    @Expose
    private String receiptStatus;
    @SerializedName("invoice_status")
    @Expose
    private String invoiceStatus;
    @SerializedName("amount_total")
    @Expose
    private Double amountTotal;
    @SerializedName("order_lines")
    @Expose
    private List<PurchaseOrderLine> orderLines;

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

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPickingTypeId() {
        return pickingTypeId;
    }

    public void setPickingTypeId(String pickingTypeId) {
        this.pickingTypeId = pickingTypeId;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(Double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public List<PurchaseOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<PurchaseOrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}

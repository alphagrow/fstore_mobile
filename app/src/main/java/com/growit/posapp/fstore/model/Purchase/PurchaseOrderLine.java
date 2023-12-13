package com.growit.posapp.fstore.model.Purchase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchaseOrderLine {
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("qty_received")
    @Expose
    private Double qtyReceived;
    @SerializedName("qty_invoiced")
    @Expose
    private Double qtyInvoiced;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("subtotal")
    @Expose
    private Double subtotal;
    @SerializedName("taxes_id")
    @Expose
    private String taxesId;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getQtyReceived() {
        return qtyReceived;
    }

    public void setQtyReceived(Double qtyReceived) {
        this.qtyReceived = qtyReceived;
    }

    public Double getQtyInvoiced() {
        return qtyInvoiced;
    }

    public void setQtyInvoiced(Double qtyInvoiced) {
        this.qtyInvoiced = qtyInvoiced;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(String taxesId) {
        this.taxesId = taxesId;
    }

}

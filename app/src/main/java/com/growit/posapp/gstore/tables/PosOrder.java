package com.growit.posapp.gstore.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
public class PosOrder implements Serializable{
    public PosOrder() {

    }

    @PrimaryKey(autoGenerate = true)
    private int order_id;

    @ColumnInfo(name = "cropID")
    private int cropID;
    @ColumnInfo(name = "totalQuantity")
    private int totalQuantity;
    @ColumnInfo(name = "productID")
    private int productID;

    public int getVariantID() {
        return variantID;
    }

    public void setVariantID(int variantID) {
        this.variantID = variantID;
    }

    @ColumnInfo(name = "variantID")
    private int variantID;

    @ColumnInfo(name = "taxid")
    private int taxID;

    @ColumnInfo(name = "unitPrice")
    private double unitPrice;

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getLineDiscount() {
        return lineDiscount;
    }

    public void setLineDiscount(int lineDiscount) {
        this.lineDiscount = lineDiscount;
    }

    @ColumnInfo(name = "lineDiscount")
    private int lineDiscount;

    @ColumnInfo(name = "discountAmount")
    private double discountAmount;

    @ColumnInfo(name = "gst")
    private int gst;

    @ColumnInfo(name = "quantity")
    private double quantity;

    public double getAmount_tax() {
        return amount_tax;
    }

    public void setAmount_tax(double amount_tax) {
        this.amount_tax = amount_tax;
    }



    private double amount_tax;

    @ColumnInfo(name = "productName")
    private String productName;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCropID() {
        return cropID;
    }

    public void setCropID(int cropID) {
        this.cropID = cropID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getTaxID() {
        return taxID;
    }

    public void setTaxID(int taxID) {
        this.taxID = taxID;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }


    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFullProductName() {
        return fullProductName;
    }

    public void setFullProductName(String fullProductName) {
        this.fullProductName = fullProductName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public String getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(String productVariants) {
        this.productVariants = productVariants;
    }

    @ColumnInfo(name = "fullProductName")
    private String fullProductName;

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    @ColumnInfo(name = "crop_name")
    private String crop_name;

    @ColumnInfo(name = "productImage")
    private String productImage;

    @ColumnInfo(name = "productAmount")
    private String productAmount;

    @ColumnInfo(name = "productVariants")
    private String productVariants;
}
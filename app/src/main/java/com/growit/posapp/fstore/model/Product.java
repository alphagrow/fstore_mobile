package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    @SerializedName("id")
    @Expose
    private String productID;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @SerializedName("product_id")
    @Expose
    private String product_id;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    @SerializedName("productImage")
    @Expose
    private String productImage;

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    @SerializedName("product_image_url")
    @Expose
    private String productImageUrl;

    @SerializedName("product_name")
    @Expose
    private String productName;

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    @SerializedName("crop_name")
    @Expose
    private String crop_name;

    @SerializedName("quantity")
    @Expose
    private Double quantity;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("tax_id")
    @Expose
    private String taxId;
    @SerializedName("subtotal")
    @Expose
    private Double subtotal;

    public String getProduct_package() {
        return product_package;
    }

    public void setProduct_package(String product_package) {
        this.product_package = product_package;
    }

    @SerializedName("product_package")
    @Expose
    private String product_package;
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Product withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Product withQuantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Product withPrice(Double price) {
        this.price = price;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Product withTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Product withSubtotal(Double subtotal) {
        this.subtotal = subtotal;
        return this;
    }

}

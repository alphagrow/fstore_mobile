package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DiscountListModel {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("minimum_amount")
    @Expose
    private Double minimumAmount;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("reward_id")
    @Expose
    private Integer rewardId;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("product_id")
    @Expose
    private Integer productId;

    public Double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Double unit_price) {
        this.unit_price = unit_price;
    }

    @SerializedName("unit_price")
    @Expose
    private Double unit_price;

    public Integer getProduct_variant_id() {
        return product_variant_id;
    }

    public void setProduct_variant_id(Integer product_variant_id) {
        this.product_variant_id = product_variant_id;
    }

    @SerializedName("product_variant_id")
    @Expose
    private Integer product_variant_id;

    @SerializedName("qty")
    @Expose
    private Integer qty;

    public Integer getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(Integer taxesId) {
        this.taxesId = taxesId;
    }

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("taxes_id")
    @Expose
    private Integer taxesId;
    @SerializedName("product_image_url")
    @Expose
    private String productImageUrl;

    public void setMinimumAmount(Double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}

package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PriceData {

    @SerializedName("product_id")
    @Expose
    private int productId;
    @SerializedName("product_variant")
    @Expose
    private String productVariant;
    @SerializedName("product_variant_name")
    @Expose
    private String productVariantName;
    @SerializedName("price_extra")
    @Expose
    private Double priceExtra;
    @SerializedName("mrp_price")
    @Expose
    private Double mrpPrice;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    public String getProductVariantName() {
        return productVariantName;
    }

    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
    }

    public Double getPriceExtra() {
        return priceExtra;
    }

    public void setPriceExtra(Double priceExtra) {
        this.priceExtra = priceExtra;
    }

    public Double getMrpPrice() {
        return mrpPrice;
    }

    public void setMrpPrice(Double mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

}
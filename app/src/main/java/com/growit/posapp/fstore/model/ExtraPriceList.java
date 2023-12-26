package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtraPriceList {

    @SerializedName("price_extra")
    @Expose
    private Double price_extra;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    public Double getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(Double mrp_price) {
        this.mrp_price = mrp_price;
    }

    @SerializedName("mrp_price")
    @Expose
    private Double mrp_price;

    public Double getPrice_extra() {
        return price_extra;
    }

    public void setPrice_extra(Double price_extra) {
        this.price_extra = price_extra;
    }

    public String getProduct_variant_name() {
        return product_variant_name;
    }

    public void setProduct_variant_name(String product_variant_name) {
        this.product_variant_name = product_variant_name;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @SerializedName("product_variant_name")
    @Expose
    private String product_variant_name;


    public String getProduct_variant_id() {
        return product_variant_id;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setProduct_variant_id(String product_variant_id) {
        this.product_variant_id = product_variant_id;
    }

    @SerializedName("product_variant")
    @Expose
    private String product_variant_id;
}
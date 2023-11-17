package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductVariantQuantity {

    @SerializedName("variant_display_name")
    @Expose
    private String variantDisplayName;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    public String getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(String variant_id) {
        this.variant_id = variant_id;
    }

    @SerializedName("variant_id")
    @Expose
    private String variant_id;

    public String getVariantDisplayName() {
        return variantDisplayName;
    }

    public void setVariantDisplayName(String variantDisplayName) {
        this.variantDisplayName = variantDisplayName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}

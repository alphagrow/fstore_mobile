package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExtraVariantData {
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("image_url")
    @Expose
    private Boolean imageUrl;
    @SerializedName("variants")
    @Expose
    private List<PriceData> variants;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Boolean imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<PriceData> getVariants() {
        return variants;
    }

    public void setVariants(List<PriceData> variants) {
        this.variants = variants;
    }
}

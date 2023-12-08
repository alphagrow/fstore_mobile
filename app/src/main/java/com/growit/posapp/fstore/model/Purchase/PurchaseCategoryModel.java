package com.growit.posapp.fstore.model.Purchase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.growit.posapp.fstore.model.Product;

import java.util.List;

public class PurchaseCategoryModel {
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("products")
    @Expose
    private List<PurchaseProductModel> products;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<PurchaseProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<PurchaseProductModel> products) {
        this.products = products;
    }
}


package com.growit.posapp.fstore.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @SerializedName("total_quantity")
    @Expose
    private Integer totalQuantity;

    @SerializedName("default_code")
    @Expose
    private Boolean defaultCode;
    @SerializedName("uom_id")
    @Expose
    private String uomId;
    @SerializedName("list_price")
    @Expose
    private Double listPrice;
    @SerializedName("standard_price")
    @Expose
    private Double standardPrice;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("product_package")
    @Expose
    private String product_package;
    @SerializedName("category_name")
    @Expose
    private String category_name;
    public String getProduct_package() {
        return product_package;
    }

    public void setProduct_package(String product_package) {
        this.product_package = product_package;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<ProductVariantQuantity> getProductVariantQuantities() {
        return productVariantQuantities;
    }

    public void setProductVariantQuantities(List<ProductVariantQuantity> productVariantQuantities) {
        this.productVariantQuantities = productVariantQuantities;
    }

    @SerializedName("product_variant_quantities")
    @Expose
    private List<ProductVariantQuantity> productVariantQuantities;

    public int getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(int taxesId) {
        this.taxesId = taxesId;
    }

    @SerializedName("taxes_id")
    @Expose
    private int taxesId;
    @SerializedName("pos_category_ids")
    @Expose
    private List<String> posCategoryIds;
    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Datum withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Datum withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Datum withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Boolean getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(Boolean defaultCode) {
        this.defaultCode = defaultCode;
    }

    public Datum withDefaultCode(Boolean defaultCode) {
        this.defaultCode = defaultCode;
        return this;
    }

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public Datum withUomId(String uomId) {
        this.uomId = uomId;
        return this;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    public Datum withListPrice(Double listPrice) {
        this.listPrice = listPrice;
        return this;
    }

    public Double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public Datum withStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
        return this;
    }





    public List<String> getPosCategoryIds() {
        return posCategoryIds;
    }

    public void setPosCategoryIds(List<String> posCategoryIds) {
        this.posCategoryIds = posCategoryIds;
    }

    public Datum withPosCategoryIds(List<String> posCategoryIds) {
        this.posCategoryIds = posCategoryIds;
        return this;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Datum withAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }


    public List<ExtraPriceList> getExtraPriceLists() {
        return extraPriceLists;
    }

    public void setExtraPriceLists(List<ExtraPriceList> extraPriceLists) {
        this.extraPriceLists = extraPriceLists;
    }

    @SerializedName("extra_price_lists")
    @Expose
    private List<ExtraPriceList> extraPriceLists;
}

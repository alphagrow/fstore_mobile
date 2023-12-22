package com.growit.posapp.fstore.model.Purchase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.growit.posapp.fstore.model.Attribute;
import com.growit.posapp.fstore.model.ExtraPriceList;
import com.growit.posapp.fstore.model.ProductVariantQuantity;

import java.util.List;

public class PurchaseProductModel {
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("detailed_type")
    @Expose
    private String detailedType;
    @SerializedName("default_code")
    @Expose
    private Boolean defaultCode;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("uom_id")
    @Expose
    private String uomId;
    @SerializedName("uom_po_id")
    @Expose
    private String uomPoId;
    @SerializedName("list_price")
    @Expose
    private Double listPrice;
    @SerializedName("technical_pest")
    @Expose
    private String technicalPest;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("mkd_by")
    @Expose
    private String mkdBy;
    @SerializedName("batch_number")
    @Expose
    private String batchNumber;
    @SerializedName("cir_no")
    @Expose
    private String cirNo;
    @SerializedName("which_crop")
    @Expose
    private Boolean whichCrop;
    @SerializedName("which_pest")
    @Expose
    private String whichPest;
    @SerializedName("mfd_date")
    @Expose
    private String mfdDate;
    @SerializedName("exp_date")
    @Expose
    private String expDate;
    @SerializedName("non_gov_product")
    @Expose
    private String nonGovProduct;
    @SerializedName("product_package")
    @Expose
    private String productPackage;
    @SerializedName("taxes_id")
    @Expose
    private Integer taxesId;
    @SerializedName("taxes_name")
    @Expose
    private String taxes_name;

    public String getTaxes_name() {
        return taxes_name;
    }

    public void setTaxes_name(String taxes_name) {
        this.taxes_name = taxes_name;
    }

    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes;
    @SerializedName("extra_price_lists")
    @Expose
    private List<ExtraPriceList> extraPriceLists;
    @SerializedName("product_variant_quantities")
    @Expose
    private List<ProductVariantQuantity> productVariantQuantities;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetailedType() {
        return detailedType;
    }

    public void setDetailedType(String detailedType) {
        this.detailedType = detailedType;
    }

    public Boolean getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(Boolean defaultCode) {
        this.defaultCode = defaultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public String getUomPoId() {
        return uomPoId;
    }

    public void setUomPoId(String uomPoId) {
        this.uomPoId = uomPoId;
    }

    public Double getListPrice() {
        return listPrice;
    }

    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    public String getTechnicalPest() {
        return technicalPest;
    }

    public void setTechnicalPest(String technicalPest) {
        this.technicalPest = technicalPest;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getMkdBy() {
        return mkdBy;
    }

    public void setMkdBy(String mkdBy) {
        this.mkdBy = mkdBy;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getCirNo() {
        return cirNo;
    }

    public void setCirNo(String cirNo) {
        this.cirNo = cirNo;
    }

    public Boolean getWhichCrop() {
        return whichCrop;
    }

    public void setWhichCrop(Boolean whichCrop) {
        this.whichCrop = whichCrop;
    }

    public String getWhichPest() {
        return whichPest;
    }

    public void setWhichPest(String whichPest) {
        this.whichPest = whichPest;
    }

    public String getMfdDate() {
        return mfdDate;
    }

    public void setMfdDate(String mfdDate) {
        this.mfdDate = mfdDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getNonGovProduct() {
        return nonGovProduct;
    }

    public void setNonGovProduct(String nonGovProduct) {
        this.nonGovProduct = nonGovProduct;
    }

    public String getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(String productPackage) {
        this.productPackage = productPackage;
    }

    public Integer getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(Integer taxesId) {
        this.taxesId = taxesId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<ExtraPriceList> getExtraPriceLists() {
        return extraPriceLists;
    }

    public void setExtraPriceLists(List<ExtraPriceList> extraPriceLists) {
        this.extraPriceLists = extraPriceLists;
    }

    public List<ProductVariantQuantity> getProductVariantQuantities() {
        return productVariantQuantities;
    }

    public void setProductVariantQuantities(List<ProductVariantQuantity> productVariantQuantities) {
        this.productVariantQuantities = productVariantQuantities;
    }

}

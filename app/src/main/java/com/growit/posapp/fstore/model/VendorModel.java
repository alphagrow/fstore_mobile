package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VendorModel {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("vendors")
    @Expose
    private List<VendorModelList> vendors;
    @SerializedName("warehouses")
    @Expose
    private List<WarehouseModel> warehouses;

    public List<WarehouseModel> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<WarehouseModel> warehouses) {
        this.warehouses = warehouses;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public List<VendorModelList> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorModelList> vendors) {
        this.vendors = vendors;
    }
}

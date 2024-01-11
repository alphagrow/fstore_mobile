package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UomCategoryModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uom_lines")
    @Expose
    private List<UomLineModel> uomLines;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UomLineModel> getUomLines() {
        return uomLines;
    }

    public void setUomLines(List<UomLineModel> uomLines) {
        this.uomLines = uomLines;
    }

}

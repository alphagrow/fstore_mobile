package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UomLineModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("l10n_in_code")
    @Expose
    private String l10nInCode;

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

    public String getL10nInCode() {
        return l10nInCode;
    }

    public void setL10nInCode(String l10nInCode) {
        this.l10nInCode = l10nInCode;
    }

}

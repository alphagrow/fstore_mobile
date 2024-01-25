package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StateModel {
    private int id;
    private String GST_NO;
    private String state;
    private String district_id;
    private String taluka_id;
    private String state_id;

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getTaluka_id() {
        return taluka_id;
    }

    public void setTaluka_id(String taluka_id) {
        this.taluka_id = taluka_id;
    }

    public String getGST_NO() {
        return GST_NO;
    }

    public void setGST_NO(String GST_NO) {
        this.GST_NO = GST_NO;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name="";

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    private Boolean active;
}

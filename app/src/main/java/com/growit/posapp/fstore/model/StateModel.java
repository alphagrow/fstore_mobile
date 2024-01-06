package com.growit.posapp.fstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StateModel {
    private int id;
    private String GST_NO;
    private String state;

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

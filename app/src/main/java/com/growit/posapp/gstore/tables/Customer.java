package com.growit.posapp.gstore.tables;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Customer implements Serializable {

    @PrimaryKey(autoGenerate = false)
    private int customer_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "mobile")
    private String mobile;
    @ColumnInfo(name = "customer_type")
    private String customer_type;
    @ColumnInfo(name = "discounts")
    private String discounts;

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    @ColumnInfo(name = "email")
    private String email;

    public String getGst_no() {
        return gst_no;
    }

    public void setGst_no(String gst_no) {
        this.gst_no = gst_no;
    }

    @ColumnInfo(name = "gst_no")
    private String gst_no;

    public String getLand_size() {
        return land_size;
    }

    public void setLand_size(String land_size) {
        this.land_size = land_size;
    }

    @ColumnInfo(name = "land_size")
    private String land_size;

    @ColumnInfo(name = "zipcode")
    private String zipcode;

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    @ColumnInfo(name = "state")
    private String state;

    @ColumnInfo(name = "district")
    private String district;

    @ColumnInfo(name = "taluka")
    private String taluka;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @ColumnInfo(name = "street")
    private String street;
}
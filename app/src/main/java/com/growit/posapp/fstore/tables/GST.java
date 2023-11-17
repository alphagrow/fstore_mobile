package com.growit.posapp.fstore.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class GST implements Serializable {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "gstId")
    private int gstId;

    public int getGstId() {
        return gstId;
    }

    public void setGstId(int gstId) {
        this.gstId = gstId;
    }

    public int getGstValue() {
        return gstValue;
    }

    public void setGstValue(int gstValue) {
        this.gstValue = gstValue;
    }

    @ColumnInfo(name = "gstValue")
    private int gstValue;

}

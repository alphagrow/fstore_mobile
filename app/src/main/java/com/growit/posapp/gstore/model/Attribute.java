
package com.growit.posapp.gstore.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

    @SerializedName("attribute_name")
    @Expose
    private String attributeName;
    @SerializedName("values")
    @Expose
    private List<Value> values;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Attribute withAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public Attribute withValues(List<Value> values) {
        this.values = values;
        return this;
    }

}

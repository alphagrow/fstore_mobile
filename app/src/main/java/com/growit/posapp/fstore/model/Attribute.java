
package com.growit.posapp.fstore.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

    @SerializedName("attribute_name")
    @Expose
    private String attributeName;
    @SerializedName("is_attribute")
    @Expose
    private Boolean isAttribute;
    public Integer getAttribute_id() {
        return attribute_id;
    }

    public Boolean getAttribute() {
        return isAttribute;
    }

    public void setAttribute(Boolean attribute) {
        isAttribute = attribute;
    }

    public void setAttribute_id(Integer attribute_id) {
        this.attribute_id = attribute_id;
    }

    @SerializedName("attribute_id")
    @Expose
    private Integer attribute_id;
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

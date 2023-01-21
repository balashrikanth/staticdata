package com.stonex.corp.payments.staticdata.model;

public class GenericValue {
    private String value;
    private String valueDataType;//STRING, INTEGER, DOUBLE, FLOAT, DATE, BOOLEAN

    public GenericValue() {
    }

    public GenericValue(String value) {
        this.value = value;
        this.valueDataType="STRING";
    }

    public GenericValue(String value, String valueDataType) {
        this.value = value;
        this.valueDataType = valueDataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueDataType() {
        return valueDataType;
    }

    public void setValueDataType(String valueDataType) {
        this.valueDataType = valueDataType;
    }
}

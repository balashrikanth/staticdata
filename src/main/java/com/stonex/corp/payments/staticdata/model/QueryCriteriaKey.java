package com.stonex.corp.payments.staticdata.model;

public class QueryCriteriaKey {
    private String criteriaKey;
    private GenericValue genericValue;
    private String operator;//GT, LT, GE, LE, BT, EQ, NE


    public QueryCriteriaKey() {
    }
    public QueryCriteriaKey(String criteriaKey, String value) {
        this.criteriaKey = criteriaKey;
        this.genericValue = new GenericValue(value,"STRING");
        this.operator = "EQ";
    }


    public QueryCriteriaKey(String criteriaKey, GenericValue genericValue) {
        this.criteriaKey = criteriaKey;
        this.genericValue = genericValue;
        this.operator = "EQ";
    }

    public QueryCriteriaKey(String criteriaKey, GenericValue genericValue, String valueDataType) {
        this.criteriaKey = criteriaKey;
        this.genericValue = genericValue;
        this.operator = "EQ";

    }

    public String getCriteriaKey() {
        return criteriaKey;
    }

    public void setCriteriaKey(String criteriaKey) {
        this.criteriaKey = criteriaKey;
    }

    public GenericValue getGenericValue() {
        return genericValue;
    }

    public void setGenericValue(GenericValue genericValue) {
        this.genericValue = genericValue;
    }



    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

package com.stonex.corp.payments.staticdata.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.error.AppError;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class StaticData {
    @JsonIgnore
    public String getCollectionName(){
        return "";
    }
    public String createPK(String content) {
        return "";
    }
    @JsonIgnore
    public String getPK(){
        return "";
    }
    public String getJSONString(Document document){
        return "";
    }

    public StaticData getObjectFromDocument(Document document){
        StaticData staticData = new StaticData();

        return staticData;
    }

    @JsonIgnore
    public Picklist getPickListHeaders(){
        return new Picklist();
    }
    @JsonIgnore
    public String[] getPickListRow(Document document){
        return new String[SystemFieldConfig.PICKLISTCOLS];
    }

    public List<AppError> fieldValidate(String content){
        return new ArrayList<AppError>();
    }

}

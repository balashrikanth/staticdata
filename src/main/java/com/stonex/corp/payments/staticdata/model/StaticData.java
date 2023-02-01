package com.stonex.corp.payments.staticdata.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.MongoCollection;
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
        return document.toJson();
    }



    public void enrichFields(){

    }

    public StaticData getObjectFromDocument(Document document){
        StaticData staticData = new StaticData();

        return staticData;
    }

    @JsonIgnore
    public String getReportTitle(){
        return "";
    }
    @JsonIgnore
    public String getReportHeader(){
        return "";
    }

    @JsonIgnore
    public String getReportFooter(){
        return "";
    }


    @JsonIgnore
    public List<Document> getReportData(MongoCollection<Document> collection){
        return new ArrayList<Document>();
    }


    @JsonIgnore
    public Picklist getPickListHeaders(){
        return new Picklist();
    }
    @JsonIgnore
    public String[] getPickListRow(Document document){
        return new String[SystemFieldConfig.PICKLISTCOLS];
    }

    public String[] getLabels(){
        String [] stringList = new String[10];
        return stringList;
    }
    public AppError fieldValidate(String content){
        return new AppError();
    }

}

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
        return content;
    }
    @JsonIgnore
    public String getPK(){
        return "";
    }
    public String getJSONString(Document document){
        return document.toJson();
    }



    public void enrichFields(){
        //Dummy For enrichment if required

    }

    public StaticData getObjectFromDocument(Document document){
        // Skeleton class for signature to override in individual domain classes
            return new StaticData();
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
        return new String[10];
    }
    public AppError fieldValidate(String content){
        return new AppError();
    }

}

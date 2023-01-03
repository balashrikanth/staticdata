package com.stonex.corp.payments.staticdata.model;



import org.bson.Document;



public class StaticData {
    public String getCollectionName(){
        return "";
    }
    public String createPK(String content) {
        return "";
    }
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
}

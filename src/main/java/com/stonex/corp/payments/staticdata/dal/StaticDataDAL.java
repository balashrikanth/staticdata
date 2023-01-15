package com.stonex.corp.payments.staticdata.dal;


import org.bson.Document;

import java.util.List;

public interface StaticDataDAL {
    public Document findRecord(boolean approved, String collectionName, String staticDataPK);
    public Document getRecord(boolean approved,String collectionName, String staticDataPK);
    public List<Document> getFiltered(boolean approved, String collectionName,  String filterJSONString);
    public List<Document> getFilteredPartialApproved(String collectionName,  String filterJSONString);
    public List<Document> getAll(boolean approved, String collectionName);
    public List<Document> getAllActive(boolean approved, String collectionName);
    public long getCount(boolean approved, String collectionName);
    public Document createUnapproved(String collectionName,  String staticDataPK, String content);
    public Document createApproved(String collectionName, String staticDataPK, String content);
    public Document editUnapproved(String collectionName,  String staticDataPK, String content);
    public Document editApproved(String collectionName,  String staticDataPK, String content);
    public boolean undo(String collectionName,  String staticDataPK);
    public Document deleteApproved(String collectionName,  String staticDataPK, String content);
    public boolean removeApproved(String collectionName,  String staticDataPK, String content);
}

package com.stonex.corp.payments.staticdata.dal;



import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.model.PageInfo;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;
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
    public List<Object> getAll(boolean approved, String functionId, Query query);
    public List<Object> getPage(boolean approved, String functionId, Query query, PageInfo pageInfo);
    public StaticDataMetaInfoDB saveMetaData(String staticDataPK, String collectionName, String userId, String action, String approveRemark);
    public StaticDataMetaInfoDB getMetaData(String staticDataPK, String collectionName);
    public StaticDataAuditDB saveAuditData(String staticDataPK, String functionId, String collectionName,String content, StaticDataMetaInfoDB staticDataMetaInfoDB);
}

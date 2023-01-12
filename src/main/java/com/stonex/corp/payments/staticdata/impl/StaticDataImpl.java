package com.stonex.corp.payments.staticdata.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StaticDataImpl implements StaticDataDAL {
    @Autowired
    private final MongoTemplate mongoTemplate;

    public StaticDataImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public Document findRecord(boolean approved, String collectionName,  String staticDataPK){
        List<Document> aggregateDocList = new ArrayList<Document>();
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }
        Document document = new Document();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            Document staticDataPKMatch =  new Document("$match", new Document("staticDataPK", String.valueOf(staticDataPK)));
            aggregateDocList.add(staticDataPKMatch);
            AggregateIterable<Document> resultDocument = collection.aggregate(aggregateDocList);
            document = resultDocument.first();
        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }



    @Override
    public Document getRecord( boolean approved, String collectionName,  String staticDataPK){
        List<Document> aggregateDocList = new ArrayList<Document>();
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }
        Document document = new Document();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            Document staticDataPKMatch =  new Document("$match", new Document("staticDataPK", String.valueOf(staticDataPK)));
            aggregateDocList.add(staticDataPKMatch);
            AggregateIterable<Document> resultDocument = collection.aggregate(aggregateDocList);
            document = resultDocument.first();
            if (document!=null){
                document.remove("staticDataPK");
                document.remove("_id");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }



    @Override
    public List<Document> getFiltered( boolean approved, String collectionName,  String filterJSONString){
        List<Document> resultDocumentList = new ArrayList<Document>();

        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            Document filter = new Document();
            filter = Document.parse(filterJSONString);
            Document document = new Document();
            FindIterable<Document> resultDocument = collection.find(filter);
            for (Document d : resultDocument ){
                d.remove("staticDataPK");
                d.remove("_id");
                resultDocumentList.add(d);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultDocumentList;
    }



    @Override
    public List<Document> getFilteredPartialApproved(String collectionName, String filterString) {
    return null;
    }

    @Override
    public List<Document> getAll(boolean approved, String collectionName){

        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        List<Document> aggregateDocList = new ArrayList<Document>();
        List<Document> resultDocumentList = new ArrayList<Document>();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            FindIterable<Document> resultDocument = collection.find();
            for (Document d : resultDocument ){
                if (d!=null){
                    d.remove("staticDataPK");
                    d.remove("_id");
                }
                resultDocumentList.add(d);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultDocumentList;
    }

    @Override
    public long getCount(boolean approved, String collectionName) {
        long counter = 0 ;
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        List<Document> aggregateDocList = new ArrayList<Document>();
        List<Document> resultDocumentList = new ArrayList<Document>();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            counter = collection.countDocuments();

        } catch (Exception e){
            e.printStackTrace();
        }
        return counter;
    }

    @Override
    public Document createUnapproved(String collectionName,  String staticDataPK, String content){
        Document document = new Document();
        try {
            //Insert New Unapproved
            Document document1 = Document.parse(content);
            document1.append("staticDataPK",staticDataPK);
            document = mongoTemplate.insert(document1,collectionName.concat("_unapproved"));
            document.remove("staticDataPK");
            document.remove("_id");
        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }
    @Override
    public Document createApproved(String collectionName, String staticDataPK, String content){
        Document document = new Document();
        try {
            //First Remove Old
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            mongoTemplate.remove(query,collectionName.concat("_unapproved"));
            //Insert New Approved Record
            Document document1 = Document.parse(content);
            document1.append("staticDataPK",staticDataPK);
            document = mongoTemplate.insert(document1,collectionName);
            document.remove("staticDataPK");
            document.remove("_id");
        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }

    @Override
    public Document editUnapproved(String collectionName,  String staticDataPK, String content){
        Document document = new Document();
        try {
            //First Remove Old
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            mongoTemplate.remove(query,collectionName.concat("_unapproved"));
            //Create New
            Document document1 = Document.parse(content);
            document1.append("staticDataPK",staticDataPK);
            document = mongoTemplate.insert(document1,collectionName.concat("_unapproved"));
            document.remove("staticDataPK");
            document.remove("_id");
        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }

    @Override
    public Document editApproved(String collectionName, String staticDataPK, String content) {
        Document document = new Document();
        try {
            //First Remove Old Unapproved
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            mongoTemplate.remove(query,collectionName.concat("_unapproved"));
            //Removed Old Approved
            //First Remove Old Unapproved
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            mongoTemplate.remove(query1,collectionName);
            //Create New with new values replacing old
            Document document1 = Document.parse(content);
            document1.append("staticDataPK",staticDataPK);
            document = mongoTemplate.insert(document1,collectionName);
            document.remove("staticDataPK");
            document.remove("_id");

        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }


    @Override
    public boolean undo(String collectionName,  String staticDataPK){
        boolean returnValue = false;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            DeleteResult result = mongoTemplate.remove(query,collectionName.concat("_unapproved"));
            returnValue = result.wasAcknowledged();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  returnValue;
    }
    @Override
    public Document deleteApproved(String collectionName,  String staticDataPK, String content) {
        Document document = new Document();
        try {
            //First Remove Old
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
            mongoTemplate.remove(query, collectionName.concat("_unapproved"));
            //Create New Unapproved.
            Document document1 = Document.parse(content);
            document1.append("staticDataPK", staticDataPK);
            document = mongoTemplate.insert(document1, collectionName.concat("_unapproved"));
            document.remove("staticDataPK");
            document.remove("_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    @Override
    public boolean removeApproved(String collectionName,  String staticDataPK, String content) {
        boolean returnValue =  false;
        try {
            //First Completely from Approved
            Query query = new Query();
            query.addCriteria(Criteria.where("staticDataPK").is(staticDataPK));
           DeleteResult result = mongoTemplate.remove(query, collectionName);
           returnValue = result.wasAcknowledged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

}

package com.stonex.corp.payments.staticdata.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.model.PageInfo;

import com.stonex.corp.payments.staticdata.repository.StaticDataAuditDBRepository;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class StaticDataImpl implements StaticDataDAL {
    private static final Logger logger = LogManager.getLogger(StaticDataImpl.class);


    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;
    @Autowired
    StaticDataAuditDBRepository staticDataAuditDBRepository;
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            FindIterable<Document> resultDocument = collection.find(filter);
            for (Document d : resultDocument ){
                d.remove("staticDataPK");
                d.remove("_id");
                resultDocumentList.add(d);
            }
        } catch (Exception e){
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
        }
        return resultDocumentList;
    }

    @Override
    public  MongoCollection<Document> getAllAsReport(boolean approved, String collectionName) {
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            return  collection;
        } catch (Exception e){
            logger.error(e.getMessage());
            return  null;
        }
    }

    @Override
    public Document getSpecificRecord(boolean approved, String collectionName, String staticDataPK) {
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        Document document = new Document();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            AggregateIterable<Document> resultDocument = collection.aggregate(Arrays.asList(new Document("$match", new Document("staticDataPK", staticDataPK))));
            if (resultDocument.first()!=null){
                document = resultDocument.first();
                document.remove("staticDataPK");
                document.remove("_id");
            }

        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return document;
    }

    @Override
    public List<Document> getAllActive(boolean approved, String collectionName) {
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }

        List<Document> resultDocumentList = new ArrayList<>();
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            FindIterable<Document> resultDocument = collection.find(new Document("$match",
                    new Document("active", true)));
            for (Document d : resultDocument ){
                if (d!=null){
                    d.remove("staticDataPK");
                    d.remove("_id");
                }
                resultDocumentList.add(d);
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return resultDocumentList;
    }

    @Override
    public long getCount(boolean approved, String collectionName) {
        long counter = 0 ;
        if (!approved){
            collectionName = collectionName.concat("_unapproved");
        }


        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            counter = collection.countDocuments();

        } catch (Exception e){
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
        }
        return returnValue;
    }

    @Override
    public List<Object> getAll(boolean approved, String functionId, Query query) {
        List<Object> objectList = new ArrayList<Object>();
        try {
            StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
            String collectionName = staticDataFactory.getCollectionName();
            if (!approved) {
                collectionName = collectionName.concat("_unapproved");
            }
            objectList = mongoTemplate.find(query,Object.class,collectionName);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return objectList;
    }


    @Override
    public List<Object> getPage(boolean approved, String functionId,Query query, PageInfo pageInfo) {
        List<Object> objectList = new ArrayList<>();
        try {
            StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
            String collectionName = staticDataFactory.getCollectionName();
            if (!approved) {
                collectionName = collectionName.concat("_unapproved");
            }
            Stream<?> streamList = mongoTemplate.find(query,staticDataFactory.getClass(),collectionName).stream().skip(pageInfo.getPage()).limit(pageInfo.getSize());
            objectList = streamList.collect(Collectors.toList());
            for (Object o : objectList){
                objectList.add(o);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return objectList;
    }

    @Override
    public StaticDataMetaInfoDB saveMetaData(String staticDataPK, String collectionName, String userId, String action, String approveRemark) {
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPKAndCollectionName(staticDataPK,collectionName);
        StaticDataMetaInfoDB staticDataMetaInfoDB2;
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( staticDataPK,  collectionName, userId, action);
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,action,approveRemark);
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        return staticDataMetaInfoDB2;
    }

    @Override
    public StaticDataMetaInfoDB getMetaData(String staticDataPK, String collectionName) {
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPKAndCollectionName(staticDataPK,collectionName);
        return staticDataMetaInfoDB;
    }

    @Override
    public StaticDataAuditDB saveAuditData(String staticDataPK, String functionId, String collectionName,String content, StaticDataMetaInfoDB staticDataMetaInfoDB) {
        String action = staticDataMetaInfoDB.getLastAudit().getAction();
        StaticDataAuditDB staticDataAuditDB = null;
        String oldContent="";
        String newContent=content;//Always current content is new content
        switch (action.toUpperCase()){
            case SystemFieldConfig.ACTIONNEW:
                newContent = content;//For New Old Content is empty;
                break;
            case SystemFieldConfig.ACTIONEDIT:
            case SystemFieldConfig.ACTIONAPPROVE:
            case SystemFieldConfig.ACTIONDELETE:
                //Audit Old and New
                int previousVersion = staticDataMetaInfoDB.getVersion()-1;
                if (previousVersion<1){
                    previousVersion = 1;
                }
                staticDataAuditDB = staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataPK,collectionName,previousVersion);
                //New Content for previous update becomes old content and version increases to next one.
                if (staticDataAuditDB!=null && staticDataAuditDB.getNewcontent()!=null ){
                    oldContent = staticDataAuditDB.getNewcontent();//New content of previous becomes old for next
                }
                //Set New Content
                if (action.equalsIgnoreCase(SystemFieldConfig.ACTIONDELETE)){
                    newContent="";//For delete scenarios
                }
                break;
        }
        StaticDataAuditDB staticDataAuditDB1 = new StaticDataAuditDB(functionId,oldContent,newContent,staticDataMetaInfoDB.getVersion());
        StaticDataAuditDB staticDataAuditDB2 = this.staticDataAuditDBRepository.save(staticDataAuditDB1);
        return  staticDataAuditDB2;

    }

    @Override
    public int getLastVersion(String staticDataPK, String collectionName) {
        int counter = 0;
        StaticDataMetaInfoDB staticDataMetaInfoDB = this.staticDataMetaInfoDBRepository.findFirstByStaticDataPKAndCollectionName(staticDataPK,collectionName);
        if (staticDataMetaInfoDB!=null && staticDataMetaInfoDB.getVersion()>0){
            counter = staticDataMetaInfoDB.getVersion();
        }
        return counter;
    }

}

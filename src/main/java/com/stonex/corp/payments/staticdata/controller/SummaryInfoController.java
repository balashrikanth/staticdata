package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.model.Audit;
import com.stonex.corp.payments.staticdata.model.StaticData;
import com.stonex.corp.payments.staticdata.model.StaticDataWithAudit;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/summary")
public class SummaryInfoController {
    @Autowired
    StaticDataDAL staticDataDAL;


    @GetMapping("/all")
    public String getAllSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId){
        String jsonContent = "";
        AppReturnObject appReturnObject = new AppReturnObject();
        List<StaticDataWithAudit> staticDataWithAuditList = new ArrayList<StaticDataWithAudit>();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        List<Document> documentList = staticDataDAL.getAll(approved,staticDataFactory.getCollectionName());
        for (Document d : documentList){
            StaticDataFactory staticDataFactory1 = new StaticDataFactory(functionId, d.toJson());
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory1.getPKValue(),staticDataFactory1.getCollectionName());
            Audit audit = new Audit();
            StaticDataWithAudit staticDataWithAudit;
            try {
                audit = staticDataMetaInfoDB.getLastAudit();
                 staticDataWithAudit = new StaticDataWithAudit(staticDataFactory.getStaticData(),audit);
            } catch (Exception e){
                e.printStackTrace();
                staticDataWithAudit = new StaticDataWithAudit(staticDataFactory.getStaticData());
            }
            staticDataWithAuditList.add(staticDataWithAudit);
        }
        appReturnObject.PerformReturnArrayObject(staticDataWithAuditList);
        return appReturnObject.setReturnJSON();
    }


    @PostMapping("/filtered")
    public String getFilteredSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        List<StaticDataWithAudit> staticDataWithAuditList = new ArrayList<StaticDataWithAudit>();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        List<Document> documentList = staticDataDAL.getFiltered(approved,staticDataFactory.getCollectionName(),jsonContent);
        for (Document d : documentList){
            StaticDataFactory staticDataFactory1 = new StaticDataFactory(functionId, d.toJson());
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory1.getPKValue(),staticDataFactory1.getCollectionName());
            Audit audit = new Audit();
            StaticDataWithAudit staticDataWithAudit;
            try {
                audit = staticDataMetaInfoDB.getLastAudit();
                staticDataWithAudit = new StaticDataWithAudit(staticDataFactory.getStaticData(),audit);
            } catch (Exception e){
                e.printStackTrace();
                staticDataWithAudit = new StaticDataWithAudit(staticDataFactory.getStaticData());
            }
            staticDataWithAuditList.add(staticDataWithAudit);
        }
        appReturnObject.PerformReturnArrayObject(staticDataWithAuditList);
        return appReturnObject.setReturnJSON();

    }

    @GetMapping("/count/function/{function}/approved/{approved}")
    public long getRecordCount(@PathVariable("function") String functionId,@RequestHeader(value = "userid", defaultValue = "SYSTEM") String userId, @PathVariable("approved") boolean approved){
        AppReturnObject appReturnObject = new AppReturnObject();
        long counter=0;
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        counter = staticDataDAL.getCount(approved,staticDataFactory.getCollectionName());
        return counter;

    }

}

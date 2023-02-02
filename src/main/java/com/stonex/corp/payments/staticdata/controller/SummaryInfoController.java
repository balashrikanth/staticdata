package com.stonex.corp.payments.staticdata.controller;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.mongodb.client.MongoCollection;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.dto.ReportView;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.model.Audit;
import com.stonex.corp.payments.staticdata.model.StaticDataWithAudit;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/summary")
public class SummaryInfoController {

    private static final Logger logger = LogManager.getLogger(SummaryInfoController.class);


    @Autowired
    StaticDataDAL staticDataDAL;


    @GetMapping("/all")
    public String getAllSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId){
        AppReturnObject appReturnObject = new AppReturnObject();
        List<StaticDataWithAudit> staticDataWithAuditList = new ArrayList<>();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        List<Document> documentList = staticDataDAL.getAll(approved,staticDataFactory.getCollectionName());
        for (Document d : documentList){
            StaticDataFactory staticDataFactory1 = new StaticDataFactory(functionId, d.toJson());
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory1.getPKValue(),staticDataFactory1.getCollectionName());
            if (staticDataMetaInfoDB!=null){
                Audit audit = new Audit();
                StaticDataWithAudit staticDataWithAudit;
                try {
                    audit = staticDataMetaInfoDB.getLastAudit();
                    staticDataWithAudit = new StaticDataWithAudit(staticDataFactory1.getStaticData(),audit);
                } catch (Exception e){
                    logger.error("Unable to Get audit but system procceeds".concat(e.getMessage()));
                    staticDataWithAudit = new StaticDataWithAudit(staticDataFactory1.getStaticData());

                }
                staticDataWithAuditList.add(staticDataWithAudit);
            }
        }
        appReturnObject.PerformReturnArrayObject(staticDataWithAuditList);
        return appReturnObject.setReturnJSON();
    }


    @PostMapping("/filtered")
    public String getFilteredSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        List<StaticDataWithAudit> staticDataWithAuditList = new ArrayList<>();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        List<Document> documentList = staticDataDAL.getFiltered(approved,staticDataFactory.getCollectionName(),jsonContent);
        for (Document d : documentList){
            StaticDataFactory staticDataFactory1 = new StaticDataFactory(functionId, d.toJson());
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory1.getPKValue(),staticDataFactory1.getCollectionName());
            if (staticDataMetaInfoDB!=null){
                Audit audit = new Audit();
                StaticDataWithAudit staticDataWithAudit;
                try {
                    audit = staticDataMetaInfoDB.getLastAudit();
                    staticDataWithAudit = new StaticDataWithAudit(staticDataFactory1.getStaticData(),audit);
                } catch (Exception e){
                    logger.error("Unable to Get audit but system procceeds".concat(e.getMessage()));
                    staticDataWithAudit = new StaticDataWithAudit(staticDataFactory1.getStaticData());
                }
                staticDataWithAuditList.add(staticDataWithAudit);
            }
        }
        appReturnObject.PerformReturnArrayObject(staticDataWithAuditList);
        return appReturnObject.setReturnJSON();

    }

    @GetMapping("/count/function/{function}/approved/{approved}")
    public long getRecordCount(@PathVariable("function") String functionId,@RequestHeader(value = "userid", defaultValue = "SYSTEM") String userId, @PathVariable("approved") boolean approved){
        long counter=0;
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        try {
            counter = staticDataDAL.getCount(approved,staticDataFactory.getCollectionName());
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return counter;

    }

    @GetMapping("/report")
    public String getSummaryReport(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId) {
        AppReturnObject appReturnObject = new AppReturnObject();
        ReportView reportView = new ReportView();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        List<Document> documentList = new ArrayList<>();
        List<String> reportList = new ArrayList<>();
        MongoCollection<Document> documentMongoCollection = staticDataDAL.getAllAsReport(approved,staticDataFactory.getCollectionName());
        reportView.setTitle(staticDataFactory.getStaticData().getReportTitle());
        reportView.setHeader(staticDataFactory.getStaticData().getReportHeader());
        if (documentMongoCollection.countDocuments()>0){
            documentList = staticDataFactory.getStaticData().getReportData(documentMongoCollection);
        }
        JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
        for (Document d : documentList){
            String reportline = JsonFlattener.flatten(d.toJson(settings));
            JSONObject jsonObject = new JSONObject(reportline);
            reportline = normalize(jsonObject).toString();
            reportList.add(reportline);
        }
        reportView.setBody(reportList.toString());
        reportView.setFooter(staticDataFactory.getStaticData().getReportFooter());
        appReturnObject.PerformReturnArrayObject(reportView);
        return appReturnObject.setReturnJSON();
    }

    static JSONObject normalize(JSONObject object) throws JSONException {
        JSONObject result = new JSONObject();
        Iterator<String> iterator = object.keys();

        while (iterator.hasNext()) {
            String key =  iterator.next();
            String normalizedKey = key.replace(".", "_");

            Object inner = object.get(key);

            if (inner instanceof JSONObject) {
                result.put(normalizedKey, normalize((JSONObject) inner));
            } else if (inner instanceof String) {
                result.put(normalizedKey, object.getString(key).replace("&", "_"));
            } else {
                result.put(normalizedKey, inner);
            }
        }

        return result;
    }
}

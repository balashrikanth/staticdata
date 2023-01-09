package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/summary")
public class SummaryInfoController {
    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;

    @GetMapping("/all")
    public String getAllSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId){
        String jsonContent = "";
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        List<Document> documentList = staticDataDAL.getAll(approved,staticDataFactory.getCollectionName());
        appReturnObject.PerformReturnArrayObject(staticDataFactory.getObjectFromDocumentList(staticDataMetaInfoDBRepository,documentList));
        return appReturnObject.setReturnJSON();
    }


    @PostMapping("/filtered")
    public String getFilteredSummary(@RequestHeader("approved") boolean approved,@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,"");
        List<Document> documentList = staticDataDAL.getFiltered(approved,staticDataFactory.getCollectionName(),jsonContent);
        appReturnObject.PerformReturnArrayObject(staticDataFactory.getObjectFromDocumentList(staticDataMetaInfoDBRepository,documentList));
        return appReturnObject.setReturnJSON();

    }


}

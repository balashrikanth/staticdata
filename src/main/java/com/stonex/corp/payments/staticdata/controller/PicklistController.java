package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/picklist")
public class PicklistController {

    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;

    @GetMapping("/all")
    public String getAllList( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = "SYSTEM") String userId){
        String jsonContent = "";
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        //get all Approved
        List<Document> documentList = staticDataDAL.getAll(true,staticDataFactory.getCollectionName());
        Picklist picklist = staticDataFactory.getPickListHeaders();
        picklist.addRows(staticDataFactory.getPickListFromDocument(documentList));
        appReturnObject.PerformReturnArrayObject(picklist);
        return appReturnObject.setReturnJSON();

    }

    @GetMapping("/specific/functionId/{functionId}/recordkey/{recordkey}")
    public String getSpecificItemByFunctionAndKey( @PathVariable("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = "SYSTEM") String userId, @PathVariable("recordkey") String recordkey){
        String jsonContent = "";
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        //get all Approved
        Document document = staticDataDAL.getSpecificRecord(true,staticDataFactory.getCollectionName(), recordkey.toUpperCase());//Key is always Uppercase
        appReturnObject.PerformReturnArrayObject(document);
        return appReturnObject.setReturnJSON();

    }


    @PostMapping("/filtered")
    public String getFilteredList( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        //get all Approved
        List<Document> documentList = staticDataDAL.getFiltered(true,staticDataFactory.getCollectionName(),jsonContent);
        Picklist picklist = staticDataFactory.getPickListHeaders();
        picklist.addRows(staticDataFactory.getPickListFromDocument(documentList));
        appReturnObject.PerformReturnArrayObject(picklist);
        return appReturnObject.setReturnJSON();

    }

}

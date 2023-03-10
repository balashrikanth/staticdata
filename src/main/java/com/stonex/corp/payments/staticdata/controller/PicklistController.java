package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.TemplateDB;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.repository.TemplateDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/picklist")
public class PicklistController {

    private static final Logger logger = LogManager.getLogger(PicklistController.class);


    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;
    @Autowired
    TemplateDBRepository templateDBRepository;

    @GetMapping("/all")
    public String getAllList( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId){
        logger.debug("GET API All");
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        //get all Approved
        List<Document> documentList = staticDataDAL.getAll(true,staticDataFactory.getCollectionName());
        Picklist picklist = staticDataFactory.getPickListHeaders();
        picklist.addRows(staticDataFactory.getPickListFromDocument(documentList));
        appReturnObject.PerformReturnArrayObject(picklist);
        return appReturnObject.setReturnJSON();

    }

    @GetMapping("/specific/function/{functionid}/recordkey/{recordkey}")
    public String getSpecificItemByFunctionAndKey( @PathVariable("functionid") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @PathVariable("recordkey") String recordkey){
        logger.debug("GET API Specific Record Key");
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId);
        //get all Approved
        Document document = staticDataDAL.getSpecificRecord(true,staticDataFactory.getCollectionName(), recordkey.toUpperCase());//Key is always Uppercase
        if (document!=null){
            appReturnObject.PerformReturnArrayObject(document);
        }
        return appReturnObject.setReturnJSON();

    }

    @GetMapping("/template/keytype/{keytype}")
    public String getTemplateKeys(@PathVariable("keytype") String keytype){
        logger.debug("GET API Template Keys");
        AppReturnObject appReturnObject = new AppReturnObject();
        List<TemplateDB> templateDBList = templateDBRepository.findAllByTemplatetypeAndActive(keytype,true);
        if (!templateDBList.isEmpty()){
            Picklist picklist = new Picklist();
            picklist.setNoOfCols(1);
            picklist.setPickListHeaders(new String[]{"Allowed Keys"});
            for (TemplateDB templateDB : templateDBList){
                if (templateDB.getKey()!=null){
                    picklist.addSingleRow(new String[]{templateDB.getKey()});
                }
            }
            appReturnObject.PerformReturnArrayObject(picklist);
        }
        return appReturnObject.setReturnJSON();
    }

    @PostMapping("/filtered")
    public String getFilteredList( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestBody String jsonContent){
        logger.debug("POST API Get Filtered");
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

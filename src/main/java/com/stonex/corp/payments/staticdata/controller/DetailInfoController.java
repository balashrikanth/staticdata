package com.stonex.corp.payments.staticdata.controller;


import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.ErrorDataDAL;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dal.ValidateDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;

import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.error.AppError;

import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Locale;


@CrossOrigin(origins = "*")
@RestController

@RequestMapping("/detail")
public class DetailInfoController {

    private static final Logger logger = LogManager.getLogger(DetailInfoController.class);


    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    ErrorDataDAL errorDataDAL;
    @Autowired
    ValidateDataDAL validateDataDAL;



    @PostMapping("/new")
    public String createNew(@RequestHeader("functionId") String functionId,  @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader(value = "language", defaultValue = "en") String language, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        } else {
            jsonContent = staticDataFactory.getContent();
        }
        appError = validateDataDAL.validate(staticDataFactory,language);
        if (appError.getDetails().size()==0){
            Document docapproved = staticDataDAL.findRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
            Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
            //Create Unapproved only if not found in Approved and Unapproved Collections
            if (docapproved==null && docunapproved==null){
                Document d = staticDataDAL.createUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                //if there is a past Static Meta data record already available because of create and delete and then fresh create - reuse it else create new.
                StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONNEW,"");
                //Audit Old and New
                staticDataDAL.saveAuditData(staticDataFactory.getPKValue(),functionId,staticDataFactory.getCollectionName(),jsonContent,staticDataMetaInfoDB);

                //Complete
                appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB,d));
            } else {
                appError = new AppError();
                appError.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9001","code"));
                appReturnObject.addError(appError);
            }
        } else {
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("editableRecord")
    public String getEditable(@RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader(value = "language", defaultValue = "en") String language, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        jsonContent=staticDataFactory.getContent();
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        Document docapproved = staticDataDAL.getRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        Document docunapproved = staticDataDAL.getRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB,docunapproved));
        } else if (docapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB,docapproved));
        } else {
            appError = new AppError();
            appError.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9005",functionId));
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("/edit")
    public String edit( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader(value = "language", defaultValue = "en") String language, @RequestHeader(value = "version", defaultValue = "1") int version, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }else {
            jsonContent = staticDataFactory.getContent();
        }
        int dbVersion = this.staticDataDAL.getLastVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        if (dbVersion!=version){
            appError = versionMismatch(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        appError = validateDataDAL.validate(staticDataFactory,language);
        if (appError.getDetails().size()==0){
            Document d = staticDataDAL.editUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONEDIT,"");

            //Audit Old and New
            staticDataDAL.saveAuditData(staticDataFactory.getPKValue(),functionId,staticDataFactory.getCollectionName(),jsonContent,staticDataMetaInfoDB);
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB,d));
        } else {
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();
    }

    //Delete Unapproved is like Undo
    @PostMapping("/undo")
    public String delete( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader(value = "language", defaultValue = "en") String language,@RequestHeader(value = "version", defaultValue = "1") int version, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }else {
            jsonContent = staticDataFactory.getContent();
        }
        int dbVersion = this.staticDataDAL.getLastVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        if (dbVersion!=version){
            appError = versionMismatch(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        boolean  result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONDELETE,"");

        //Audit Old and New
       staticDataDAL.saveAuditData(staticDataFactory.getPKValue(),functionId,staticDataFactory.getCollectionName(),jsonContent,staticDataMetaInfoDB);

        appReturnObject.PerformReturnObject(result);
        return appReturnObject.setReturnJSON();
    }

    //Delete Approved - creates a new unapproved and it is pending approval
    @PostMapping("/delete")
    public String deleteApproved( @RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId,@RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader(value = "language", defaultValue = "en") String language,@RequestHeader(value = "version", defaultValue = "1") int version, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }else {
            jsonContent = staticDataFactory.getContent();
        }
        int dbVersion = this.staticDataDAL.getLastVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        if (dbVersion!=version){
            appError = versionMismatch(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        Document d = staticDataDAL.deleteApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(), jsonContent);
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONDELETE,"");

        //Audit Old and New
        staticDataDAL.saveAuditData(staticDataFactory.getPKValue(),functionId,staticDataFactory.getCollectionName(),jsonContent,staticDataMetaInfoDB);

        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB,d));
        return appReturnObject.setReturnJSON();
    }

    @PostMapping("/approve")
    public String approve(@RequestHeader("functionId") String functionId, @RequestHeader(value = "applicationId", defaultValue = "STATICDATA") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestHeader("remark") String approveRemark, @RequestHeader(value = "language", defaultValue = "en") String language, @RequestHeader(value = "version", defaultValue = "1") int version,@RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        AppError appError = isValidPayload(jsonContent,functionId);
        if (appError!=null){
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        if (staticDataFactory.getStaticData()==null){
            appError = incorrectStructure(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }else {
            jsonContent = staticDataFactory.getContent();
        }
        int dbVersion = this.staticDataDAL.getLastVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        if (dbVersion!=version){
            appError = versionMismatch(functionId);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved==null){
            AppError appError1 = new AppError();
            appError1.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9005",functionId));
            appReturnObject.addError(appError);
        } else {
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataDAL.getMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
            if (staticDataMetaInfoDB == null ){
                appError = new AppError();
                appError.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9002",functionId));
                appReturnObject.addError(appError);
            } else if ( staticDataMetaInfoDB.getLastAudit().getCreatorId().equalsIgnoreCase(userId)){
                //Approver cannot be same as creator
                appError = new AppError();
                appError.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9002",functionId));
                appReturnObject.addError(appError);
            } else {
                //remove the unapproved record as now it will need to move to approved
                boolean result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
                if (result){
                    StaticDataMetaInfoDB staticDataMetaInfoDB1 = null;
                    switch(staticDataMetaInfoDB.getLastAudit().getAction().toUpperCase()){
                        case SystemFieldConfig.ACTIONDELETE:
                            //If it was an approval for Delete then the main record itself should go away
                            boolean returnValue = staticDataDAL.removeApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                            staticDataMetaInfoDB1 = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                            appReturnObject.PerformReturnObject(returnValue);
                            break;
                        case SystemFieldConfig.ACTIONEDIT:
                            //if it is edit
                            Document d = staticDataDAL.editApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                            staticDataMetaInfoDB1 = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB1,d));
                            break;
                        case SystemFieldConfig.ACTIONNEW:
                            //New
                            Document d1 = staticDataDAL.createApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                            staticDataMetaInfoDB1 = staticDataDAL.saveMetaData(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDB1,d1));
                            break;
                        default:
                            appReturnObject.PerformReturnObject(false);
                            break;
                    }
                    //Audit Old and New
                    staticDataDAL.saveAuditData(staticDataFactory.getPKValue(),functionId,staticDataFactory.getCollectionName(),jsonContent,staticDataMetaInfoDB1);


                } else {
                    appError = new AppError();
                    appError.addErrorItem(errorDataDAL.getErrorItem(language,"ESD9003",functionId));
                    appReturnObject.addError(appError);
                }
            }
        }
        return appReturnObject.setReturnJSON();
    }

    public boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public AppError isValidPayload(String json, String functionId){
        AppError appError = null;
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            appError = new AppError();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(SystemFieldConfig.MESSAGECODEWORD, "Invalid PayLoad Data");
            appError.addErrorItem(errorDataDAL.getErrorItem("en",SystemFieldConfig.DEFAULTERRORCODE,functionId,hashMap));
        }
        return appError;
    }

    public AppError incorrectStructure(String functionId){
        AppError appError = new AppError();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(SystemFieldConfig.MESSAGECODEWORD, "Incorrect Structure of Payload data");
        appError.addErrorItem(errorDataDAL.getErrorItem("en",SystemFieldConfig.DEFAULTERRORCODE,functionId,hashMap));
        return appError;
    }

    public AppError versionMismatch(String functionId){
        AppError appError = new AppError();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(SystemFieldConfig.MESSAGECODEWORD, "Another user has updated the record in the meantime");
        appError.addErrorItem(errorDataDAL.getErrorItem("en",SystemFieldConfig.DEFAULTERRORCODE,functionId,hashMap));
        return appError;
    }




}

package com.stonex.corp.payments.staticdata.controller;


import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import com.stonex.corp.payments.staticdata.repository.StaticDataAuditDBRepository;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController

@RequestMapping("/detail")
public class DetailInfoController {

    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;
    @Autowired
    StaticDataAuditDBRepository staticDataAuditDBRepository;

    @PostMapping("/new")
    public String createNew(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        AppError appError = staticDataFactory.fieldValidate(jsonContent);
        if (appError.getDetails().size()==0){
            Document docapproved = staticDataDAL.findRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
            Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
            //Create Unapproved only if not found in Approved and Unapproved Collections
            if (docapproved==null && docunapproved==null){
                Document d = staticDataDAL.createUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                StaticDataMetaInfoDB staticDataMetaInfoDB = new StaticDataMetaInfoDB(jsonContent,  functionId, userId, SystemFieldConfig.ACTIONNEW);
                StaticDataMetaInfoDB staticDataMetaInfoDB1 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                //Audit Old and New
                StaticDataAuditDB staticDataAuditDB = new StaticDataAuditDB(functionId,"",jsonContent,staticDataMetaInfoDB1.getVersion());
                staticDataAuditDBRepository.save(staticDataAuditDB);
                //Complete
                appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
            } else {
                appError = new AppError();
                ErrorItem errorItem = new ErrorItem("EN0001","code","Record Key Already Exists as Unapproved or Approved");
                appError.addErrorItem(errorItem);
                appReturnObject.setReturncode(false);
                appReturnObject.addError(appError);
            }
        } else {
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("editableRecord")
    public String getEditable(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docapproved = staticDataDAL.getRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        Document docunapproved = staticDataDAL.getRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docunapproved));
        } else if (docapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docapproved));
        } else {
            AppError appError = new AppError();
            ErrorItem errorItem = new ErrorItem("EN0003","code","Record Key Does not exist as Approved or Unapproved");
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("/edit")
    public String edit( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        AppError appError = staticDataFactory.fieldValidate(jsonContent);
        if (appError.getDetails().size()==0){
            Document d = staticDataDAL.editUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
            StaticDataMetaInfoDB staticDataMetaInfoDB2;
            if (staticDataMetaInfoDB ==null){
                StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONEDIT);
                staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
            } else {
                staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONEDIT,"");
                staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
            }
            //Audit Old and New
            int previousVersion = staticDataMetaInfoDB2.getVersion()-1;
            if (previousVersion<1){
                previousVersion = 1;
            }
            StaticDataAuditDB staticDataAuditDB = staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),previousVersion);
            //New Content for previous update becomes new content and version increases to next one.
            String oldContent="";
            if (staticDataAuditDB!=null && staticDataAuditDB.getNewcontent()!=null ){
                oldContent = staticDataAuditDB.getNewcontent();//New content of previous becomes old for next
            }
            StaticDataAuditDB staticDataAuditDB1 = new StaticDataAuditDB(functionId,oldContent,jsonContent,previousVersion+1);
            staticDataAuditDBRepository.save(staticDataAuditDB1);
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        } else {
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();
    }

    //Delete Unapproved is like Undo
    @PostMapping("/undo")
    public String delete( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        boolean  result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        StaticDataMetaInfoDB staticDataMetaInfoDB2;
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONDELETE);
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONDELETE,"");
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        //Audit Old and New
        int previousVersion = staticDataMetaInfoDB2.getVersion()-1;
        if (previousVersion<1){
            previousVersion = 1;
        }
        StaticDataAuditDB staticDataAuditDB = staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),previousVersion);
        //New Content for previous update becomes new content and version increases to next one.
        String oldContent="";
        if (staticDataAuditDB!=null && staticDataAuditDB.getNewcontent()!=null ){
            oldContent = staticDataAuditDB.getNewcontent();//New content of previous becomes old for next
        }
        StaticDataAuditDB staticDataAuditDB1 = new StaticDataAuditDB(functionId,oldContent,"",previousVersion+1);
        staticDataAuditDBRepository.save(staticDataAuditDB1);
        appReturnObject.PerformReturnObject(result);
        return appReturnObject.setReturnJSON();
    }

    //Delete Approved - creates a new unapproved and it is pending approval
    @PostMapping("/delete")
    public String deleteApproved( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document d = staticDataDAL.deleteApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(), jsonContent);
        StaticDataMetaInfoDB staticDataMetaInfoDB2;
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONDELETE);
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONDELETE,"");
            staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        //Audit Old and New
        int previousVersion = staticDataMetaInfoDB2.getVersion()-1;
        if (previousVersion<1){
            previousVersion = 1;
        }
        StaticDataAuditDB staticDataAuditDB = staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),previousVersion);
        //New Content for previous update becomes new content and version increases to next one.
        String oldContent="";
        if (staticDataAuditDB!=null && staticDataAuditDB.getNewcontent()!=null ){
            oldContent = staticDataAuditDB.getNewcontent();//New content of previous becomes old for next
        }
        StaticDataAuditDB staticDataAuditDB1 = new StaticDataAuditDB(functionId,oldContent,"",previousVersion+1);
        staticDataAuditDBRepository.save(staticDataAuditDB1);
        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        return appReturnObject.setReturnJSON();
    }

    @PostMapping("/approve")
    public String approve(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestHeader("remark") String approveRemark, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved==null){
            AppError appError = new AppError();
            ErrorItem errorItem = new ErrorItem("EN0002","code","Record Key does not exist as Unapproved");
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        } else {
            StaticDataMetaInfoDB staticDataMetaInfoDB2;
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
            if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getCreatorId().equalsIgnoreCase(userId)){
                //Approver cannot be same as creator
                AppError appError = new AppError();
                ErrorItem errorItem = new ErrorItem("EN0009",functionId,"Approver cannot be same as person who created Record");
                appError.addErrorItem(errorItem);
                appReturnObject.setReturncode(false);
                appReturnObject.addError(appError);
            } else {
                //remove the unapproved record as now it will need to move to approved
                boolean result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
                String newContent="";
                if (result){
                    if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getAction().equalsIgnoreCase(SystemFieldConfig.ACTIONDELETE)){
                        //If it was an approval for Delete then the main record itself should go away
                        boolean returnValue = staticDataDAL.removeApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                        staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                        staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                        appReturnObject.PerformReturnObject(returnValue);
                    } else if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getAction().equalsIgnoreCase(SystemFieldConfig.ACTIONEDIT)) {
                        //if it is edit
                        newContent = jsonContent;
                        Document d = staticDataDAL.editApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                        staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                        staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
                    }
                    else {
                        //New
                        newContent = jsonContent;
                        Document d = staticDataDAL.createApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                        staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                        staticDataMetaInfoDB2 = staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
                    }
                    //Audit Old and New
                    int previousVersion = staticDataMetaInfoDB2.getVersion()-1;
                    if (previousVersion<1){
                        previousVersion = 1;
                    }
                    StaticDataAuditDB staticDataAuditDB = staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),previousVersion);
                    //New Content for previous update becomes new content and version increases to next one.
                    String oldContent="";
                    if (staticDataAuditDB!=null && staticDataAuditDB.getNewcontent()!=null ){
                        oldContent = staticDataAuditDB.getNewcontent();//New content of previous becomes old for next
                    }
                    StaticDataAuditDB staticDataAuditDB1 = new StaticDataAuditDB(functionId,oldContent,newContent,previousVersion+1);
                    staticDataAuditDBRepository.save(staticDataAuditDB1);

                } else {
                    AppError appError = new AppError();
                    ErrorItem errorItem = new ErrorItem("EN0009",functionId,"Unable to Move from Unapproved to Approved");
                    appError.addErrorItem(errorItem);
                    appReturnObject.setReturncode(false);
                    appReturnObject.addError(appError);
                }
            }
        }
        return appReturnObject.setReturnJSON();
    }


}

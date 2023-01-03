package com.stonex.corp.payments.staticdata.controller;


import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/detail")
public class DetailInfoController {

    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;

    @PostMapping("/new")
    public String createNew(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docapproved = staticDataDAL.findRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        //Create Unapproved only if not found in Approved and Unapproved Collections
        if (docapproved==null && docunapproved==null){
            Document d = staticDataDAL.createUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
            StaticDataMetaInfoDB staticDataMetaInfoDB = new StaticDataMetaInfoDB(jsonContent,  functionId, userId, SystemFieldConfig.ACTIONNEW);
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        } else {
            AppError appError = new AppError("EN0001","Record Key Already Exists as Unapproved or Approved","E");
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();
    }

    @GetMapping("editableRecord")
    public String getEditable(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docapproved = staticDataDAL.getRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        Document docunapproved = staticDataDAL.getRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docunapproved));
        } else if (docunapproved == null && docapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docapproved));
        } else {
            AppError appError = new AppError("EN0003","Record Key Does not exist as Approved or Unapproved","E");
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("/edit")
    public String edit( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document d = staticDataDAL.editUnapproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONEDIT);
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONEDIT,"");
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        return appReturnObject.setReturnJSON();
    }

    //Delete Unapproved is like Undo
    @PostMapping("/undo")
    public String delete( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        boolean  result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONDELETE);
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONDELETE,"");
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        appReturnObject.PerformReturnObject(result);
        return appReturnObject.setReturnJSON();
    }

    //Delete Approved - creates a new unapproved and it is pending approval
    @PostMapping("/delete")
    public String deleteApproved( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document d = staticDataDAL.deleteApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(), jsonContent);
        StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
        if (staticDataMetaInfoDB ==null){
            StaticDataMetaInfoDB staticDataMetaInfoDB1 = new StaticDataMetaInfoDB( jsonContent,  functionId, userId, SystemFieldConfig.ACTIONDELETE);
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB1);
        } else {
            staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONDELETE,"");
            staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
        }
        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        return appReturnObject.setReturnJSON();
    }

    @PostMapping("/approve")
    public String approve(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestHeader("remark") String approveRemark, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved==null){
            AppError appError = new AppError("EN0002","Record Key does not exist as Unapproved","E");
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        } else {
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
            if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getCreatorId().equalsIgnoreCase(userId)){
                //Approver cannot be same as creator
                AppError appError = new AppError("EN0009","Approver cannot be same as person who created Record","E");
                appReturnObject.setReturncode(false);
                appReturnObject.addError(appError);
            } else {
                //remove the unapproved record as now it will need to move to approved
                boolean result = staticDataDAL.undo(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
                if (result = true){
                    if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getAction().equalsIgnoreCase(SystemFieldConfig.ACTIONDELETE)){
                        //If it was an approval for Delete then the main record itself should go away
                        boolean returnValue = staticDataDAL.removeApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                        staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                        staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                        appReturnObject.PerformReturnObject(returnValue);
                    } else {
                        //if is new or Edit than Create Approved record
                        Document d = staticDataDAL.createApproved(staticDataFactory.getCollectionName(),staticDataFactory.getPKValue(),jsonContent);
                        staticDataMetaInfoDB.update(userId,SystemFieldConfig.ACTIONAPPROVE,approveRemark);
                        staticDataMetaInfoDBRepository.save(staticDataMetaInfoDB);
                        appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,d));
                    }
                } else {
                    AppError appError = new AppError("EN0003","Unable to Move from Unapproved to Approved","E");
                    appReturnObject.setReturncode(false);
                    appReturnObject.addError(appError);
                }
            }
        }
        return appReturnObject.setReturnJSON();
    }

}

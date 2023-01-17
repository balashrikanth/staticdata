package com.stonex.corp.payments.staticdata.controller;


import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.EntityValidationRulesDB;
import com.stonex.corp.payments.staticdata.entity.ErrorCodeDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import com.stonex.corp.payments.staticdata.model.FieldAttributes;
import com.stonex.corp.payments.staticdata.model.FieldValidationRules;
import com.stonex.corp.payments.staticdata.repository.EntityValidationRulesDBRepository;
import com.stonex.corp.payments.staticdata.repository.ErrorCodeDBRepository;
import com.stonex.corp.payments.staticdata.repository.StaticDataAuditDBRepository;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;


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
    @Autowired
    ErrorCodeDBRepository errorCodeDBRepository;
    @Autowired
    EntityValidationRulesDBRepository entityValidationRulesDBRepository;

    @PostMapping("/new")
    public String createNew(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        //AppError appError = staticDataFactory.fieldValidate(jsonContent);
        AppError appError = validate(staticDataFactory);
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
                ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ESD9001");
                errorCodeDB.parseKeywords();
                ErrorItem errorItem = errorCodeDB.getErrorItem("code");
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
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docapproved = staticDataDAL.getRecord(true,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        Document docunapproved = staticDataDAL.getRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docunapproved));
        } else if (docapproved!=null){
            appReturnObject.PerformReturnObject(staticDataFactory.getObjectFromDocument(staticDataMetaInfoDBRepository,docapproved));
        } else {
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ESD9005");
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        }
        return appReturnObject.setReturnJSON();

    }

    @PostMapping("/edit")
    public String edit( @RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader("userid") String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
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
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
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
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
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
        if (!isValid(jsonContent)){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            HashMap<String, String> hashMap = new HashMap<String,String>();
            hashMap.put("%%MESSAGE%%", "Invalid PayLoad Data");
            errorCodeDB.parseKeywords(hashMap);
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
            return appReturnObject.setReturnJSON();
        }
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        Document docunapproved = staticDataDAL.findRecord(false,staticDataFactory.getCollectionName(),staticDataFactory.getPKValue());
        if (docunapproved==null){
            AppError appError = new AppError();
            ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ESD9005");
            errorCodeDB.parseKeywords();
            ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
            appError.addErrorItem(errorItem);
            appReturnObject.setReturncode(false);
            appReturnObject.addError(appError);
        } else {
            StaticDataMetaInfoDB staticDataMetaInfoDB2;
            StaticDataMetaInfoDB staticDataMetaInfoDB = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticDataFactory.getPKValue());
            if (staticDataMetaInfoDB !=null && staticDataMetaInfoDB.getLastAudit().getCreatorId().equalsIgnoreCase(userId)){
                //Approver cannot be same as creator
                AppError appError = new AppError();
                ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ESD9002");
                errorCodeDB.parseKeywords();
                ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
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
                    ErrorCodeDB errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ESD9003");
                    errorCodeDB.parseKeywords();
                    ErrorItem errorItem = errorCodeDB.getErrorItem(functionId);
                    appError.addErrorItem(errorItem);
                    appReturnObject.setReturncode(false);
                    appReturnObject.addError(appError);
                }
            }
        }
        return appReturnObject.setReturnJSON();
    }

    public AppError validate(StaticDataFactory staticDataFactory){
        AppError appError = new AppError();
        ErrorItem errorItem = new ErrorItem();
        ErrorCodeDB errorCodeDB = new ErrorCodeDB();
        HashMap<String,String> hashMap;
        HashMap<String,Object> fieldValueMap = staticDataFactory.getFields();
        List<EntityValidationRulesDB> entityValidationRulesDBList = entityValidationRulesDBRepository.findAllByFunctionid(staticDataFactory.getFunctionId());
        try {
            for(EntityValidationRulesDB entityValidationRulesDB : entityValidationRulesDBList){
                List<FieldValidationRules> fieldValidationRulesList = entityValidationRulesDB.getFieldvalidationrules();
                for (FieldValidationRules fieldValidationRules : fieldValidationRulesList){
                    FieldAttributes fieldAttributes = fieldValidationRules.getFieldattributes();
                    if (fieldValidationRules.getFieldid()!=null && fieldAttributes!=null){
                        Object o = fieldValueMap.get(fieldValidationRules.getFieldid());
                        //DataType Checks
                        if (fieldAttributes.getDatatype()!=null){
                            boolean dataTypeError = false;
                            switch(fieldAttributes.getDatatype().trim().toUpperCase()){
                                case "INTEGER":
                                case "FLOAT":
                                case "DOUBLE":
                                    if (o!=null && o instanceof Number){
                                        dataTypeError = false;
                                    } else{
                                        dataTypeError = true;
                                    }
                                    break;
                                case "BOOLEAN":
                                    try {
                                        boolean b = (boolean) o;
                                    } catch (Exception e){
                                        dataTypeError=true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (dataTypeError){
                                if (fieldAttributes.getDatatypeerrorcode()!=null){
                                    errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",fieldAttributes.getMandatoryerrorcode());
                                    if (errorCodeDB==null){
                                        errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for "+fieldAttributes.getMandatoryerrorcode());
                                    }
                                } else {
                                    errorCodeDB = new ErrorCodeDB("EXXXXX","Data Type Error "+fieldValidationRules.getFieldid());
                                }
                                hashMap = new HashMap<String,String>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%DATATYPE%%", fieldAttributes.getDatatype());
                                errorCodeDB.parseKeywords(hashMap);
                                errorItem = errorCodeDB.getErrorItem(fieldValidationRules.getFieldid());
                                appError.addErrorItem(errorItem);

                            }
                        }
                        //Mandatory Check
                        if (fieldAttributes.isMandatory()){
                            if (fieldAttributes.getDatatype()!=null && fieldAttributes.getDatatype().equalsIgnoreCase("STRING")){
                                if (o==null || o.toString().equalsIgnoreCase("")){
                                    if (fieldAttributes.getMandatoryerrorcode()!=null){
                                        errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",fieldAttributes.getMandatoryerrorcode());
                                        if (errorCodeDB==null){
                                            errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for "+fieldAttributes.getMandatoryerrorcode());
                                        }

                                    } else {
                                        errorCodeDB = new ErrorCodeDB("EXXXXX","Mandatory Check failed for "+fieldValidationRules.getFieldid());
                                    }
                                    hashMap = new HashMap<String,String>();
                                    hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                    errorCodeDB.parseKeywords(hashMap);
                                    errorItem = errorCodeDB.getErrorItem(fieldValidationRules.getFieldid());
                                    appError.addErrorItem(errorItem);
                                }
                            }
                        }
                        //Data Constraint Checks
                        if (fieldAttributes.getDataconstraint()!=null){
                            boolean dataTypeError = false;
                            switch(fieldAttributes.getDataconstraint().trim().toUpperCase()){
                                case "ALPHABET":
                                    if (o!=null){
                                        if (o.toString().matches("^[a-zA-Z\\s]*$")){
                                            dataTypeError = false;
                                        } else {
                                            dataTypeError = true;
                                        }
                                    }
                                    break;
                                case "ALPHANUMERIC":
                                    if (o!=null){
                                        if (o.toString().matches("^[a-zA-Z0-9\\s]*$")){
                                            dataTypeError = false;
                                        } else {
                                            dataTypeError = true;
                                        }
                                    }
                                    break;
                                case "NUMERIC":
                                    if (o!=null){
                                        if (o.toString().matches("-?\\d+(\\.\\d+)?")){
                                            dataTypeError = false;
                                        } else {
                                            dataTypeError = true;
                                        }
                                    }
                                    break;
                                case "EXTENDEDSET":
                                    if (o!=null){
                                        if (StringUtils.isAsciiPrintable(o.toString())){
                                            dataTypeError = false;
                                        } else {
                                            dataTypeError = true;
                                        }
                                    }
                                    break;
                                case "SWIFTCHARSET":
                                    break;
                                default:
                                    break;
                            }
                            if (dataTypeError){
                                if (fieldAttributes.getDataconstrainterrorcode()!=null){
                                    errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",fieldAttributes.getDataconstrainterrorcode());
                                    if (errorCodeDB==null){
                                        errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for "+fieldAttributes.getMandatoryerrorcode());
                                    }
                                } else {
                                    errorCodeDB = new ErrorCodeDB("EXXXXX","Data Constraint Error "+fieldValidationRules.getFieldid());
                                }
                                hashMap = new HashMap<String,String>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%CONSTRAINT%%", fieldAttributes.getDataconstraint());
                                errorCodeDB.parseKeywords(hashMap);
                                errorItem = errorCodeDB.getErrorItem(fieldValidationRules.getFieldid());
                                appError.addErrorItem(errorItem);
                            }

                        }

                        //MIN and MAX CHECKS
                        int fieldValue = 0;//This will be length for string and actual value for numeric
                        if (fieldAttributes.getDatatype()!=null && fieldAttributes.getMinlength()>0 && fieldAttributes.getMaxlength()>0){
                            switch(fieldAttributes.getDatatype().trim().toUpperCase()){
                                case "STRING":
                                    fieldValue = o.toString().length();
                                    break;
                                case "INTEGER":
                                    try {
                                        fieldValue = (int) o;
                                    } catch (Exception e){
                                    }
                                    break;
                                case "FLOAT":
                                    try {
                                        Float floatValue = (Float) o;
                                        fieldValue = Math.round(floatValue);
                                    } catch (Exception e){
                                    }
                                    break;
                                case "DOUBLE":
                                    try {
                                        Double doubleValue = (Double) o;
                                        fieldValue = doubleValue.intValue();
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (fieldValue<fieldAttributes.getMinlength() || fieldValue>fieldAttributes.getMaxlength()) {
                                if (fieldAttributes.getLengtherrorcode()!=null){
                                    errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",fieldAttributes.getLengtherrorcode());
                                    if (errorCodeDB==null){
                                        errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for "+fieldAttributes.getLengtherrorcode());
                                    }
                                } else {
                                    errorCodeDB = new ErrorCodeDB("EXXXXX","Data Length or Size Error "+fieldValidationRules.getFieldid());
                                }
                                hashMap = new HashMap<String,String>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%MINLENGTH%%",String.valueOf(fieldAttributes.getMinlength()));
                                hashMap.put("%%MAXLENGTH%%",String.valueOf(fieldAttributes.getMaxlength()));
                                errorCodeDB.parseKeywords(hashMap);
                                errorItem = errorCodeDB.getErrorItem(fieldValidationRules.getFieldid());
                                appError.addErrorItem(errorItem);
                            }
                        }
                        //OTHER CHECKS - TO BE ADDED LIST OF VALUES

                    }
                }

            }



        }catch (Exception e){
            e.printStackTrace();
            errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en","ENC9999");
            if (errorCodeDB==null){
                errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for ENC9999");
            }
            errorCodeDB.parseKeywords();
            errorItem = errorCodeDB.getErrorItem(staticDataFactory.getFunctionId());
            appError.addErrorItem(errorItem);
        }
        return appError;
    }

    public boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

}

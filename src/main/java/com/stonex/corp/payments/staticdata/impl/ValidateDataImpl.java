package com.stonex.corp.payments.staticdata.impl;

import com.stonex.corp.payments.staticdata.dal.ErrorDataDAL;
import com.stonex.corp.payments.staticdata.dal.ValidateDataDAL;
import com.stonex.corp.payments.staticdata.entity.EntityValidationRulesDB;
import com.stonex.corp.payments.staticdata.entity.ErrorCodeDB;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import com.stonex.corp.payments.staticdata.model.FieldAttributes;
import com.stonex.corp.payments.staticdata.model.FieldValidationRules;
import com.stonex.corp.payments.staticdata.repository.EntityValidationRulesDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class ValidateDataImpl implements ValidateDataDAL {
    private static final Logger logger = LogManager.getLogger(ValidateDataImpl.class);

    @Autowired
    EntityValidationRulesDBRepository entityValidationRulesDBRepository;
    @Autowired
    ErrorDataDAL errorDataDAL;

    @Override
    public AppError validate(StaticDataFactory staticDataFactory, String language) {
        String errorCode="";
        AppError appError = new AppError();
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
                        errorCode = "";
                        if (fieldAttributes.getDatatype()!=null){
                            boolean dataTypeError = false;
                            switch(fieldAttributes.getDatatype().trim().toUpperCase()){
                                case "INTEGER":
                                case "FLOAT":
                                case "DOUBLE":
                                    if ( o instanceof Number){
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
                                    errorCode = fieldAttributes.getDatatypeerrorcode();
                                }
                                hashMap = new HashMap<>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%DATATYPE%%", fieldAttributes.getDatatype());
                                appError.addErrorItem(errorDataDAL.getErrorItem(language,errorCode,fieldValidationRules.getFieldid(),hashMap));
                            }
                        }
                        //Mandatory Check
                        errorCode = "";
                        if (fieldAttributes.isMandatory()){
                            if (fieldAttributes.getDatatype()!=null && fieldAttributes.getDatatype().equalsIgnoreCase("STRING")){
                                if (o==null || o.toString().equalsIgnoreCase("")){
                                    if (fieldAttributes.getMandatoryerrorcode()!=null){
                                        errorCode = fieldAttributes.getMandatoryerrorcode();
                                    }
                                    hashMap = new HashMap<>();
                                    hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                    appError.addErrorItem(errorDataDAL.getErrorItem(language,errorCode,fieldValidationRules.getFieldid(),hashMap));
                                }
                            }
                        }
                        //Data Constraint Checks
                        errorCode = "";
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
                                    errorCode = fieldAttributes.getDataconstrainterrorcode();
                                }
                                hashMap = new HashMap<>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%CONSTRAINT%%", fieldAttributes.getDataconstraint());
                                appError.addErrorItem(errorDataDAL.getErrorItem(language,errorCode,fieldValidationRules.getFieldid(),hashMap));
                            }

                        }

                        //MIN and MAX CHECKS
                        errorCode = "";
                        int fieldValue = 0;//This will be length for string and actual value for numeric
                        if (fieldAttributes.getDatatype()!=null && fieldAttributes.getMinlength()>0 && fieldAttributes.getMaxlength()>0){
                            switch(fieldAttributes.getDatatype().trim().toUpperCase()){
                                case "STRING":
                                    try {
                                        if (o!=null){
                                            fieldValue = o.toString().length();
                                        }
                                    }
                                    catch (Exception e){
                                        logger.error(e.getMessage());
                                    }
                                    break;
                                case "INTEGER":
                                    try {
                                        if (o != null) {
                                            fieldValue = (int) o;
                                        }
                                    }
                                    catch (Exception e){
                                        logger.error(e.getMessage());
                                    }
                                    break;
                                case "FLOAT":
                                    try {
                                        if (o!=null){
                                            Float floatValue = (Float) o;
                                            fieldValue = Math.round(floatValue);
                                        }
                                    } catch (Exception e){
                                        logger.error(e.getMessage());
                                    }
                                    break;
                                case "DOUBLE":
                                    try {
                                        if (o!=null){
                                            Double doubleValue = (Double) o;
                                            fieldValue = doubleValue.intValue();
                                        }
                                    }catch (Exception e){
                                        logger.error(e.getMessage());
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (fieldValue<fieldAttributes.getMinlength() || fieldValue>fieldAttributes.getMaxlength()) {
                                if (fieldAttributes.getLengtherrorcode()!=null){
                                    errorCode = fieldAttributes.getLengtherrorcode();
                                }
                                hashMap = new HashMap<>();
                                hashMap.put("%%FIELDID%%", fieldValidationRules.getFieldid());
                                hashMap.put("%%MINLENGTH%%",String.valueOf(fieldAttributes.getMinlength()));
                                hashMap.put("%%MAXLENGTH%%",String.valueOf(fieldAttributes.getMaxlength()));
                                errorCodeDB.parseKeywords(hashMap);
                                appError.addErrorItem(errorDataDAL.getErrorItem(language,errorCode,fieldValidationRules.getFieldid(),hashMap));
                            }
                        }
                        //OTHER CHECKS - TO BE ADDED LIST OF VALUES

                    }
                }

            }
        }catch (Exception e){
            logger.error(e.getMessage());
            hashMap = new HashMap<>();
            hashMap.put("%%MESSAGE%%", e.getMessage());
            appError.addErrorItem(errorDataDAL.getErrorItem(language,"ENC9999",staticDataFactory.getFunctionId()));
        }
        return appError;
    }
}

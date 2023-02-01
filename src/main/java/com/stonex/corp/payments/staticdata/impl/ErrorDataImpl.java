package com.stonex.corp.payments.staticdata.impl;

import com.stonex.corp.payments.staticdata.dal.ErrorDataDAL;
import com.stonex.corp.payments.staticdata.entity.ErrorCodeDB;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import com.stonex.corp.payments.staticdata.repository.ErrorCodeDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ErrorDataImpl implements ErrorDataDAL {

    @Autowired
    ErrorCodeDBRepository errorCodeDBRepository;

    @Override
    public ErrorItem getErrorItem(String language, String errorCode, String fieldId) {
        ErrorCodeDB errorCodeDB = null;
        ErrorItem errorItem = new ErrorItem();
        if (!language.equalsIgnoreCase("en")){
             errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode(language,errorCode);
        }
        if (language.equalsIgnoreCase("en") || errorCodeDB==null ) {
            //search by english
            errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",errorCode);
        }
        if (errorCodeDB==null){
            errorCodeDB = new ErrorCodeDB("EXXXXX","No Error Description Defined for "+errorCode);
        }
        if (errorCode!=null){
            errorCodeDB.parseKeywords();
            errorItem = errorCodeDB.getErrorItem(fieldId);
        }
        return errorItem;
    }

    @Override
    public ErrorItem getErrorItem(String language, String errorCode,String fieldId,  HashMap<String, String> hashMap) {
        ErrorCodeDB errorCodeDB = null;
        ErrorItem errorItem = new ErrorItem();
        if (!language.equalsIgnoreCase("en")){
            errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode(language,errorCode);
        }
        if (language.equalsIgnoreCase("en") || errorCodeDB==null ) {
            //search by english
            errorCodeDB = this.errorCodeDBRepository.findFirstByLanguageAndErrorcode("en",errorCode);
        }
        if (errorCode!=null){
            errorCodeDB.parseKeywords(hashMap);
            errorItem = errorCodeDB.getErrorItem(fieldId);
        }
        return errorItem;
    }
}

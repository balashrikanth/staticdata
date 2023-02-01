package com.stonex.corp.payments.staticdata.dal;

import com.stonex.corp.payments.staticdata.error.ErrorItem;

import java.util.HashMap;
import java.util.Map;

public interface ErrorDataDAL {
    public ErrorItem getErrorItem(String language, String errorCode, String fieldId );

    public ErrorItem getErrorItem(String language, String errorCode, String fieldId, HashMap<String, String> hashMap);
}

package com.stonex.corp.payments.staticdata.dal;

import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;

public interface ValidateDataDAL {
    public AppError validate(StaticDataFactory staticDataFactory, String language);
}

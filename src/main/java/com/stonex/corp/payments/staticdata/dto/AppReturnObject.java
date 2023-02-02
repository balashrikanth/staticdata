package com.stonex.corp.payments.staticdata.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.controller.SummaryInfoController;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.utils.CustomCharacterEscapes;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppReturnObject {

    private static final Logger logger = LogManager.getLogger(AppReturnObject.class);

    private boolean returncode;
    private String data;
    private AppError appError;

    public AppReturnObject(){
        this.returncode = false;
        this.data = "\"No Data\"";
        this.appError = new AppError();
    }

    public AppReturnObject(boolean returncode, String data) {
        this.returncode = returncode;
        this.data = data;
        this.appError = new AppError();
    }

    public AppReturnObject(boolean returncode, String data, AppError appError) {
        this.returncode = returncode;
        this.data = data;
        if (appError != null)
            this.appError = appError;
    }


    public void PerformReturnObject (Object objdata){

        ObjectMapper mapper = new ObjectMapper();
       this.setReturncode(true);
       this.appError = new AppError();
        String jsonString;

        try {
            jsonString = mapper.writeValueAsString(objdata);
            setReturncode(true);
            setData(jsonString);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            setReturncode(false);
            setData("\"\"");
            this.appError = new AppError(SystemFieldConfig.UNEXPECTEDERROR,"Unexpected Error Preparing response","S","en") ;

        }
    }

    public void PerformReturnArrayObject (Object objdata){

        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
        this.setReturncode(true);
        this.appError = new AppError();
        String jsonString;
        try {

            jsonString = mapper.writeValueAsString(objdata);
            setReturncode(true);
            setData(jsonString);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            setReturncode(false);
            setData("\"\"");
            this.appError = new AppError(SystemFieldConfig.UNEXPECTEDERROR,"Unexpected Error Preparing response","S","en") ;
        }
    }

    public void addError(AppError appError){
        setReturncode(false);
        this.appError = appError;

    }



    public String setReturnJSON(){
        String jsonString="";
        String errorjson = "\"error\":[";
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
        try {
            jsonString = mapper.writeValueAsString(this.appError);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            this.appError = new AppError(SystemFieldConfig.UNEXPECTEDERROR,"Unexpected Error Preparing error response","S","en") ;
        }
        errorjson=errorjson+jsonString+"]";
        String s = "{\"returncode\":" + returncode + "," + "\"data\":" + data + "," + errorjson + "}";
        return s;
    }



}

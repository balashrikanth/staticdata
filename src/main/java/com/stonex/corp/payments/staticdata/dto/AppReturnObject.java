package com.stonex.corp.payments.staticdata.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.utils.CustomCharacterEscapes;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AppReturnObject {
    private boolean returncode;
    private String data;
    private List<AppError> appErrors = null;

    public AppReturnObject(){
        this.returncode = false;
        this.data = "\"No Data\"";
        this.appErrors = new ArrayList<AppError>();
    }

    public AppReturnObject(boolean returncode, String data) {
        this.returncode = returncode;
        this.data = data;
        this.appErrors = new ArrayList<AppError>();
    }

    public AppReturnObject(boolean returncode, String data, AppError appError) {
        this.returncode = returncode;
        this.data = data;
        if (appError != null)
            this.appErrors.add(appError);
    }


    public void PerformReturnObject (Object objdata){

        ObjectMapper mapper = new ObjectMapper();
       this.setReturncode(true);
        this.appErrors = new ArrayList<>();
        String jsonString;

        try {
            jsonString = mapper.writeValueAsString(objdata);
            setReturncode(true);
            setData(jsonString);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            setReturncode(false);
            setData("\"\"");
            AppError appError = new AppError("EE9998","Unexpected Error Preparing response","S","en") ;
            this.appErrors.add(appError);
        }
    }

    public void PerformReturnArrayObject (Object objdata){

        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
        this.setReturncode(true);
        this.appErrors = new ArrayList<>();
        String jsonString;
        try {

            jsonString = mapper.writeValueAsString(objdata);
            setReturncode(true);
            setData(jsonString);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            setReturncode(false);
            setData("\"\"");
            AppError appError = new AppError("EE9998","Unexpected Error Preparing response","S","en") ;
            this.appErrors.add(appError);
        }
    }

    public void addError(AppError appError){
        setReturncode(false);
        this.appErrors.add(appError);

    }

    public void addAllError(List<AppError> appError){
        setReturncode(false);
        setData("\"\"");
        this.appErrors.addAll(appError);

    }

    public String setReturnJSON(){

        // error is an array list now
        String errorjson = "\"errors\":[";
        int nooferrors = this.appErrors.size();
        for (int i=0; i<nooferrors; i++){
            errorjson = errorjson+"{\"errorcode\":\""+appErrors.get(i).getErrorCode()+"\","+"\"errormsg\":\""+appErrors.get(i).getErrorDesc()+"\"}";
            if (nooferrors>1 && i <(nooferrors-1)) {
                errorjson = errorjson +",";
            }
        }
        //put closing for array
        errorjson = errorjson +"]";
        String s = "{\"returncode\":" + returncode + "," + "\"data\":" + data + "," + errorjson + "}";
        return s;

    }

}

package com.stonex.corp.payments.staticdata.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class AppError {
    private String code;// First two letter followed by four digit(xxnnnn)
    private String message;
    private String errorType; // Three type of error type are available, those are E-error,S-success,W-warning
    private String target;
    private String language;
    private List<ErrorItem> details;


    public AppError() {
        this.language = "en";
        this.target="";
        this.details = new ArrayList<ErrorItem>();

    }

    public AppError(String code, String message, String errorType) {
        this.code = code;
        this.message = message;
        this.errorType = errorType;
        this.language = "en";
        this.target="";
        this.details = new ArrayList<ErrorItem>();

    }

    public AppError(String code, String message, String errorType, String language) {
        this.code = code;
        this.message = message;
        this.errorType = errorType;
        this.language = language;
        this.target="";
        this.details = new ArrayList<ErrorItem>();

    }

    public AppError(String code, String message, String errorType, String language, String target){
        this.code = code;
        this.message = message;
        this.errorType = errorType;
        this.language = language;
        this.target=target;
        this.details = new ArrayList<ErrorItem>();

    }

    public AppError(ErrorItem errorItem){
        this.details = new ArrayList<ErrorItem>();
        this.details.add(errorItem);
        this.code = errorItem.getCode();
        this.message = errorItem.getMessage();
        this.errorType = "E";
        this.language= "en";
        this.target = errorItem.getTarget();

    }

    public void addErrorItem(ErrorItem errorItem){
        this.details.add(errorItem);
        this.errorType = "E";
        this.target = errorItem.getTarget();
        if (this.details.size()>1){
            this.code="EXX9999";
            this.message="Multiple Errors encountered";
        } else if (this.details.size()==1){
            this.code = errorItem.getCode();
            this.message= errorItem.getMessage();
        }
    }
}


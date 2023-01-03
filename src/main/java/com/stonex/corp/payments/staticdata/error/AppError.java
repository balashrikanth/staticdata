package com.stonex.corp.payments.staticdata.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class AppError {
    private String errorCode;// First two letter followed by four digit(xxnnnn)
    private String errorDesc;
    private String errorType; // Three type of error type are available, those are E-error,S-success,W-warning
    private String language;
    private List<String> errorFields;

    public AppError() {
        this.errorCode = "EE9999";
        this.errorDesc = "Unexpected Error - Please Contact Client Service";
        this.errorType = "E";
        this.language = "en";
        this.errorFields=new ArrayList<>();
    }

    public AppError(String errorCode, String errorDesc, String errorType) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.errorType = errorType;
        this.language = "en";
        this.errorFields=new ArrayList<>();
    }

    public AppError(String errorCode, String errorDesc, String errorType, String language) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.errorType = errorType;
        this.language = language;
        this.errorFields=new ArrayList<>();
    }
}

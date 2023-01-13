package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import com.stonex.corp.payments.staticdata.model.ChangeInfo;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country extends StaticData {
    private String code;
    private String fullname;
    private String displayname;
    private String isocode;
    private String isonumericcode;
    private String phonecode;

    @Override
    public String getCollectionName(){
        return "static-country";
    }
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Country country = objectMapper.readValue(content,Country.class);
            returnValue = country.getCode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public String getPK(){
        return this.code;
    }
    @Override
    public String getJSONString(Document document){
        return document.toJson();
    }

    @Override
    public Country getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Country country = new Country();
        try {
            country = objectMapper.readValue(document.toJson(),Country.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return country;
    }



    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = "isocode";
        headers[1] = "displayname";
        headers[2] = "fullname";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get("isocode")!=null){
            picklistcols[0] = document.get("isocode").toString();
        }
        if (document.get("displayname")!=null){
            picklistcols[1] = document.get("displayname").toString();
        }
        if (document.get("fullname")!=null){
            picklistcols[2] = document.get("fullname").toString();
        }
        return picklistcols;
    }

    @Override
    public String[] getLabels(){
        String [] stringList = new String[]{"code","fullname","displayname","isocode","isonumericcode","phonecode"};
        return stringList;
    }

    @Override
    public AppError fieldValidate(String content){
        AppError appError = new AppError();
        ErrorItem errorItem = new ErrorItem();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Country country = objectMapper.readValue(content,Country.class);

            //Mandatory Fields
            if (country.getCode()==null || country.getCode().equalsIgnoreCase("")){
                 errorItem = new ErrorItem("ENC0001","code","Country Code cannot be null or empty");
                 appError.addErrorItem(errorItem);
            }
            if (country.getFullname()==null || country.getFullname().equalsIgnoreCase("")){
                errorItem = new ErrorItem("ENC0002","fullname","Country Full Name cannot be null or empty");
                appError.addErrorItem(errorItem);
            }
            if (country.getDisplayname()==null || country.getDisplayname().equalsIgnoreCase("")){
                errorItem = new ErrorItem("ENC0003","displayname","Country Display Name cannot be null or empty");
                appError.addErrorItem(errorItem);
            }
            if (country.getIsocode()==null || country.getIsocode().equalsIgnoreCase("")){
                errorItem = new ErrorItem("ENC0004","code","Country ISO Code cannot be null or empty");
                appError.addErrorItem(errorItem);
            }
            //Length Checks
            if (country.getCode()!=null && country.getCode().length()!=2){
                errorItem = new ErrorItem("ENC0005","code","Country Code should be 2 characters");
                appError.addErrorItem(errorItem);
            }
            if (country.getIsocode()!=null && country.getIsocode().length()!=2){
                errorItem = new ErrorItem("ENC0006","isocode","ISO Code should be 2 characters");
                appError.addErrorItem(errorItem);
            }

        }catch (Exception e){
            e.printStackTrace();
            appError = new AppError("ENC9999","Unexpected Error ","E");
        }
        return appError;
    }

}

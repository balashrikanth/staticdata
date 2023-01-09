package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.error.AppError;
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
    public List<AppError> fieldValidate(String content){
        List<AppError> appErrorList = new ArrayList<AppError>();
        AppError appError = new AppError();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Country country = objectMapper.readValue(content,Country.class);

            //Mandatory Fields
            if (country.getCode()==null || country.getCode().equalsIgnoreCase("")){
                 appError = new AppError("ENC0001","Country Code cannot be null or empty","E");
                 appErrorList.add(appError);
            }
            if (country.getFullname()==null || country.getFullname().equalsIgnoreCase("")){
                appError = new AppError("ENC0002","Country Full Name cannot be null or empty","E");
                appErrorList.add(appError);
            }
            if (country.getDisplayname()==null || country.getDisplayname().equalsIgnoreCase("")){
                appError = new AppError("ENC0003","Country Display Name cannot be null or empty","E");
                appErrorList.add(appError);
            }
            if (country.getIsocode()==null || country.getIsocode().equalsIgnoreCase("")){
                appError = new AppError("ENC0004","Country ISO Code cannot be null or empty","E");
                appErrorList.add(appError);
            }
            //Length Checks
            if (country.getCode()!=null && country.getCode().length()!=2){
                appError = new AppError("ENC0005","Country Code should be 2 characters","E");
                appErrorList.add(appError);
            }
            if (country.getIsocode()!=null && country.getIsocode().length()!=2){
                appError = new AppError("ENC0006","Country Code should be 2 characters","E");
                appErrorList.add(appError);
            }
            //Type checks
        }catch (Exception e){
            e.printStackTrace();
            appError = new AppError("ENC9999","Unexpected Error ","E");
            appErrorList.add(appError);
        }
        return appErrorList;
    }

}

package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import javax.print.Doc;


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

}

package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.Address;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends StaticData {
    private String ctptid;
    private String shortname;
    private String entity;
    private String companytype;
    private String accoutntype;
    private String relationshiptype;
    private String owneremail;
    private String fullname;
    private Address address;
    private String countrycode;
    private String telephone;
    private String countryofincorporation;
    private boolean dvp;

    @Override
    public String getCollectionName(){
        return "static-company";
    }
    @Override
    public String createPK(String content) {
        String returnValue = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Company company = objectMapper.readValue(content,Company.class);
            returnValue = company.getShortname();
        } catch(Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public String getPK(){
        return this.shortname;
    }

    @Override
    public String getJSONString(Document document){
        return document.toJson();
    }

    @Override
    public Company getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Company company = new Company();
        try {
            company = objectMapper.readValue(document.toJson(),Company.class);

        } catch (Exception e){
            e.printStackTrace();
        }
        return company;
    }

}

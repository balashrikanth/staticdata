package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Address;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends StaticData {
    private String ctptid;
    private String shortname;
    private String entity;
    private String companytype;
    private String accounttype;
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

    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(5);//As set below
        String [] headers = new String[5];
        headers[0] = "ctptid";
        headers[1] = "shortname";
        headers[2] = "entity";
        headers[3] = "companytype";
        headers[4] = "accounttype";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA","NA","NA"};
        if (document.get("ctptid")!=null){
            picklistcols[0] = document.get("ctptid").toString();
        }
        if (document.get("shortname")!=null){
            picklistcols[1] = document.get("shortname").toString();
        }
        if (document.get("entity")!=null){
            picklistcols[2] = document.get("entity").toString();
        }
        if (document.get("companytype")!=null){
            picklistcols[3] = document.get("companytype").toString();
        }
        if (document.get("accounttype")!=null){
            picklistcols[4] = document.get("accounttype").toString();
        }
        return picklistcols;
    }



}

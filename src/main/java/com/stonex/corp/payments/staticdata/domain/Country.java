package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String isocode;
    private String fullname;
    private String displayname;
    private String alpha3;
    private int isonumericcode;
    private String localcurrencycode;
    private int phonecode;
    private String entityid;
    private boolean allowselfregistration;
    private boolean allowpayment;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.
    @JsonIgnore
    static final String isocodetag = "isocode";
    @JsonIgnore
    static final String fullnametag = "fullname";
    @JsonIgnore
    static final String displaynametag = "displayname";


    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+ "country";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Country country = objectMapper.readValue(content,Country.class);
            returnValue = country.getIsocode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.isocode.toUpperCase();//Key always upper case
    }

    //Implement this for extracting the object from mongo collection
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

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = isocodetag;
        headers[1] = displaynametag;
        headers[2] = fullnametag;
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get(isocodetag)!=null){
            picklistcols[0] = document.get(isocodetag).toString();
        }
        if (document.get(displaynametag)!=null){
            picklistcols[1] = document.get(displaynametag).toString();
        }
        if (document.get(fullnametag)!=null){
            picklistcols[2] = document.get(fullnametag).toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        return new String[]{isocodetag,fullnametag,displaynametag,"isonumericcode","localcurrencycode","phonecode","entityid","allowselfregistration","allowpayment","active"};
    }


}

package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class Fxentity extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String entityid;
    private String fullname;
    private String displayname;
    private String supportemail;
    private int phonenumber;
    private Address address;
    private String[] products;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return "static-fxentity";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Fxentity fxentity = objectMapper.readValue(content, Fxentity.class);
            returnValue = fxentity.getEntityid();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.entityid;
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Fxentity getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Fxentity country = new Fxentity();
        try {
            country = objectMapper.readValue(document.toJson(), Fxentity.class);
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
        headers[0] = "entityid";
        headers[1] = "displayname";
        headers[2] = "fullname";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get("entityid")!=null){
            picklistcols[0] = document.get("entityid").toString();
        }
        if (document.get("displayname")!=null){
            picklistcols[1] = document.get("displayname").toString();
        }
        if (document.get("fullname")!=null){
            picklistcols[2] = document.get("fullname").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    public String[] getLabels(){
        String [] stringList = new String[]{"entityid","fullname","displayname","supportemail","phonenumber","active"};
        return stringList;
    }


}

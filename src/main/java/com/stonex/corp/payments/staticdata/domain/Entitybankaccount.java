package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entitybankaccount extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String entityid;
    private String sellcurrencycode;
    private List<BankAccount> bankAccounts;
    private String defaultdeliveryname;//which of the bank accounts in the list is the default
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+"entitybankaccount";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Entitybankaccount entitybankaccount = objectMapper.readValue(content, Entitybankaccount.class);
            returnValue = entitybankaccount.getEntityid().concat(entitybankaccount.getSellcurrencycode());
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.entityid.toUpperCase().concat(this.sellcurrencycode.toUpperCase());//Key always upper case
    }

    //Implement this if you have to fill up other fields from main fields - denormalize scenario
    @Override
    public void enrichFields(){

    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Entitybankaccount getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Entitybankaccount entitybankaccount = new Entitybankaccount();
        try {
            entitybankaccount = objectMapper.readValue(document.toJson(), Entitybankaccount.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return entitybankaccount;
    }

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = "entityid";
        headers[1] = "sellcurrencycode";
        headers[2] = "defaultdeliveryname";
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
        if (document.get("sellcurrencycode")!=null){
            picklistcols[1] = document.get("sellcurrencycode").toString();
        }
        if (document.get("defaultdeliveryname")!=null){
            picklistcols[2] = document.get("defaultdeliveryname").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        String [] stringList = new String[]{"entityid","sellcurrencycode","bankAccounts[]","defaultdeliveryname","active"};
        return stringList;
    }


}

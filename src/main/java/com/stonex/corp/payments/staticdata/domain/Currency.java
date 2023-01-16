package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String isocode;
    private String fullname;
    private String displayname;
    private int amountprecision;
    private int rateprecision;
    private int spotdays;
    private int secondstoaccept;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return "static-currency";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Currency currency = objectMapper.readValue(content, Currency.class);
            returnValue = currency.getIsocode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.isocode;
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Currency getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Currency country = new Currency();
        try {
            country = objectMapper.readValue(document.toJson(), Currency.class);
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
        headers[0] = "isocode";
        headers[1] = "displayname";
        headers[2] = "fullname";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
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

    //Implement this for audit trail- change info values to be show
    @Override
    public String[] getLabels(){
        String [] stringList = new String[]{"isocode","fullname","displayname","amountprecision","rateprecision","spotdays","secondstoaccept","active"};
        return stringList;
    }


}

package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency extends StaticData {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Currency.class);
    //KEEP ALL attributes in small case as reflection is used
    private String isocode;
    private String fullname;
    private String displayname;
    private int amountprecision;
    private int rateprecision;
    private String[] homecountries;// In which countries thie CCY is home CCY
    private String[] intermediaries;//Other countries to which ccy can be sent via intermediary
    private int spotdays;
    private int secondstoaccept;
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
        return SystemFieldConfig.ENTITYPREFIX+"currency";
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
            logger.error(e.getMessage());
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
    public Currency getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Currency country = new Currency();
        try {
            country = objectMapper.readValue(document.toJson(), Currency.class);
        } catch (Exception e){
            logger.error(e.getMessage());
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
        if (document.get(isocode)!=null){
            picklistcols[0] = document.get(isocode).toString();
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
        String [] stringList = new String[]{isocodetag,fullnametag,displaynametag,"amountprecision","rateprecision","homecountries[]","intermediaries[]","spotdays","secondstoaccept","active"};
        return stringList;
    }


}

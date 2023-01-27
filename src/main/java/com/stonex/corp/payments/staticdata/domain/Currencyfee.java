package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.XCurrencyFee;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currencyfee extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String clientsellcurrencycode;
    private double samecurrencyfee;
    private List<XCurrencyFee> clientbuycurrencies;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+ "currencyfee";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Currencyfee currencyfee = objectMapper.readValue(content, Currencyfee.class);
            returnValue = getClientsellcurrencycode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.clientsellcurrencycode.toUpperCase();//Key always upper case
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Currencyfee getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Currencyfee currencyfee = new Currencyfee();
        try {
            currencyfee = objectMapper.readValue(document.toJson(), Currencyfee.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return currencyfee;
    }

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(1);//As set below
        String [] headers = new String[1];
        headers[1] = "clientsellcurrencycode";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA"};
        if (document.get("clientsellcurrencycode")!=null){
            picklistcols[0] = document.get("clientsellcurrencycode").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        String [] stringList = new String[]{"clientsellcurrencycode","samecurrencyfee","clientbuycurrencies[]","active"};
        return stringList;
    }


}

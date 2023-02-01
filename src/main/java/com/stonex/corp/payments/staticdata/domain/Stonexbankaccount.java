package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stonexbankaccount extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String entityid;
    private String sellcurrencycode;
    private List<BankAccount> bankaccounts;
    private String defaultdeliveryname;//which of the bank accounts in the list is the default
    private String defaultoperatingaccount;//Which is the operating account
    private boolean active;//keep this attribute naming unchanged as picklist uses this.
    @JsonIgnore
    static final String entityidtag = "entityid";
    @JsonIgnore
    static final String sellcurrencycodetag = "sellcurrencycode";
    @JsonIgnore
    static final String defaultdeliverynametag = "defaultdeliveryname";

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+"stonexbankaccount";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Stonexbankaccount stonexbankaccount = objectMapper.readValue(content, Stonexbankaccount.class);
            returnValue = stonexbankaccount.getEntityid().concat(stonexbankaccount.getSellcurrencycode());
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



    //Implement this for extracting the object from mongo collection
    @Override
    public Stonexbankaccount getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Stonexbankaccount stonexbankaccount = new Stonexbankaccount();
        try {
            stonexbankaccount = objectMapper.readValue(document.toJson(), Stonexbankaccount.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return stonexbankaccount;
    }

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = entityidtag;
        headers[1] = sellcurrencycodetag;
        headers[2] = defaultdeliverynametag;
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get(entityidtag)!=null){
            picklistcols[0] = document.get(entityidtag).toString();
        }
        if (document.get(sellcurrencycodetag)!=null){
            picklistcols[1] = document.get(sellcurrencycodetag).toString();
        }
        if (document.get(defaultdeliverynametag)!=null){
            picklistcols[2] = document.get(defaultdeliverynametag).toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        return new String[]{entityidtag,sellcurrencycodetag,"bankaccounts[]",defaultdeliverynametag,"defaultoperatingaccount","active"};
    }


}

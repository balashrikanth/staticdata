package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
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
    private String code;
    private String fullname;
    private String displayname;
    private String isocode;
    private String isonumericcode;
    private int amountprecision;
    private int rateprecision;

    @Override
    public String getCollectionName(){
        return "static-currency";
    }

    @Override
    public String createPK(String content) {
      String returnValue = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Currency currency = objectMapper.readValue(content, Currency.class);
            returnValue = currency.getCode();
        } catch (Exception e){
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
    public Currency getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Currency currency = new Currency();
        try {
            currency = objectMapper.readValue(document.toJson(),Currency.class);

        } catch (Exception e){
            e.printStackTrace();
        }
        return currency;
    }

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

}

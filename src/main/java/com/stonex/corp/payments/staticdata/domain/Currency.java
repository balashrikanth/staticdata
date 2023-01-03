package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.Optional;

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

}

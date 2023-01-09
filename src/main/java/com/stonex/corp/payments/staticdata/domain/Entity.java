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

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entity extends StaticData {
    private String code;
    private String fullname;
    private String displayname;
    private Address address;
    private List<String> sellcurrencycodes;
    private List<String> buycurrencycodes;

    @Override
    public String getCollectionName(){
        return "static-entity";
    }

    @Override
    public String createPK(String content) {
        String returnValue = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Entity entity = objectMapper.readValue(content,Entity.class);
            returnValue = entity.getCode();
        } catch(Exception e){
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
    public Entity getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Entity entity = new Entity();
        try {
            entity = objectMapper.readValue(document.toJson(),Entity.class);

        } catch (Exception e){
            e.printStackTrace();
        }
        return entity;
    }
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = "code";
        headers[1] = "displayname";
        headers[2] = "fullname";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get("code")!=null){
            picklistcols[0] = document.get("code").toString();
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

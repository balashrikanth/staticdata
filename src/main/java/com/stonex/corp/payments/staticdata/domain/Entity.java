package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.Address;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

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
}

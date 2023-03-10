package com.stonex.corp.payments.staticdata.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.impl.ValidateDataImpl;
import com.stonex.corp.payments.staticdata.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class StaticDataFactory {

    private static final Logger logger = LogManager.getLogger(StaticDataFactory.class);


    private String functionId;
    private String content;//Json String from URL;
    private StaticData staticData;
    private String className;

    public StaticDataFactory(String functionId ){
        try {
            this.functionId = functionId;
            this.content = "";
            this.className = SystemFieldConfig.BASECLASS.concat(".domain.").concat(functionId);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> staticDataDBClass = classLoader.loadClass(this.className);
            Constructor<?> constructor = staticDataDBClass.getConstructor();
            this.staticData = (StaticData) constructor.newInstance();
        } catch (Exception e){
            this.staticData=null;//This indicates that the Static Data was not instantiated to correct object
            logger.error(e.getMessage());
        }
    }

    public StaticDataFactory(String functionId, String content){
        try {
            this.functionId = functionId;
            this.content = content;
            this.className = SystemFieldConfig.BASECLASS.concat(".domain.").concat(functionId);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> staticDataDBClass = classLoader.loadClass(this.className);
            Constructor<?> constructor = staticDataDBClass.getConstructor();
            this.staticData = (StaticData) constructor.newInstance();
            ObjectMapper objectMapper = new ObjectMapper();
            this.staticData = objectMapper.readValue(content,this.staticData.getClass());
            this.staticData.enrichFields();
            //If enriched update content with enriched values
            this.content = getJSONString(this.staticData);
        } catch (Exception e){
            this.staticData=null;//This indicates that the Static Data was not instantiated to correct object
            logger.error(e.getMessage());
        }
    }
    //Used for automatically applying validation to fields based on staticdata validation
    public HashMap<String,Object> getFields(){
        HashMap<String,Object> fieldValueMap = new HashMap<>();
        Field[] fields = this.staticData.getClass().getDeclaredFields();
        for (Field field : fields){
            try {
                Class<?> fieldType = field.getType();
                //We skip Boolean and Complex Data Type fields for now
                if (fieldType.getName().equalsIgnoreCase("java.lang.String") || fieldType.getName().equalsIgnoreCase("int")) {
                    Method method = this.staticData.getClass().getMethod("get" + field.getName()
                            .replaceFirst(field.getName().substring(0, 1), field.getName()
                                    .substring(0, 1).toUpperCase()));
                    Object o = method.invoke(this.staticData);
                    if (o != null) {
                        fieldValueMap.put(field.getName(), o);
                    }
                }
            } catch (NoSuchMethodException e){
                //We skip those objects that are of JsonIgnore type as there is no need to validate
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return fieldValueMap;
    }

    public String getCollectionName(){
        return this.staticData.getCollectionName();
    }
    public String getPKValue(){
        return this.staticData.createPK(content);
    }

    public ArrayList<String> getJSONFromDocument(List<Document> documentList){
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (Document d :documentList){
            String s = staticData.getJSONString(d);
            stringArrayList.add(s);
        }
        return stringArrayList;
    }




    public Object getObjectFromDocument(StaticDataMetaInfoDB staticDataMetaInfoDB, Document document){
        StaticData staticData1 = staticData.getObjectFromDocument(document);
        Audit audit = new Audit();
        try {
             audit = staticDataMetaInfoDB.getLastAudit();
            return new StaticDataWithAudit(staticData1,audit);
        } catch (Exception e){
            logger.error(e.getMessage());
            return staticData1;
        }
    }




    public Picklist getPickListHeaders(){
        return this.staticData.getPickListHeaders();
    }


    public List<String[]> getPickListFromDocument(List<Document> documentList){
        ArrayList<String[]> stringArrayList = new ArrayList<>();
        //instantiate a picklist
        for (Document d : documentList){
            stringArrayList.add(this.staticData.getPickListRow(d));
        }
        return stringArrayList;
    }


    @JsonIgnore
    public String[] getLabels(){
        return this.staticData.getLabels();
    }
    public AppError fieldValidate(String content){
        return this.staticData.fieldValidate(content);
    }

    public String getJSONString(Object o){
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
        String returnValue=null;
        try {
            // convert user object to json string and return it
            returnValue = mapper.writeValueAsString(o);
        }
        catch (Exception  e) {
            // catch various errors
            logger.error(e.getMessage());
        }
        return returnValue;
    }

}

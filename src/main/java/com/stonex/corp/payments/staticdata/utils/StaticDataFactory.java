package com.stonex.corp.payments.staticdata.utils;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.model.Audit;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import com.stonex.corp.payments.staticdata.model.StaticDataWithAudit;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class StaticDataFactory {

    private String functionId;
    private String content;//Json String from URL;
    private StaticData staticData;
    private String className;


    public StaticDataFactory(String functionId, String content){
        this.functionId = functionId;
        this.content = content;
        this.className = SystemFieldConfig.BASECLASS.concat(".domain.").concat(functionId);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> staticDataDBClass = classLoader.loadClass(this.className);
            Constructor<?> constructor = staticDataDBClass.getConstructor();
            this.staticData = (StaticData) constructor.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getCollectionName(){
        return this.staticData.getCollectionName();
    }
    public String getPKValue(){
        return this.staticData.createPK(content);
    }

    public ArrayList<String> getJSONFromDocument(List<Document> documentList){
        ArrayList<String> stringArrayList = new ArrayList<String>();
        for (Document d :documentList){
            String s = staticData.getJSONString(d);
            stringArrayList.add(s);
        }
        return stringArrayList;
    }

    public Object getObjectFromDocument(StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository, Document document){
        StaticData staticData1 = staticData.getObjectFromDocument(document);
        Audit audit = new Audit();
        try {
             audit = staticDataMetaInfoDBRepository.findFirstByStaticDataPK(staticData1.getPK()).getLastAudit();
            StaticDataWithAudit staticDataWithAudit = new StaticDataWithAudit(staticData1,audit);
            return staticDataWithAudit;
        } catch (Exception e){
            e.printStackTrace();
            return staticData1;
        }
    }

    public Object getObjectFromDocumentList(StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository, List<Document> documentList){
        ArrayList<Object> objectArrayList = new ArrayList<>();
        for (Document d : documentList){
            objectArrayList.add(getObjectFromDocument(staticDataMetaInfoDBRepository,d));
        }
        return objectArrayList;
    }

    public Picklist getPickListHeaders(){
        return this.staticData.getPickListHeaders();
    }

    public List<String[]> getPickListFromDocument(List<Document> documentList){
        ArrayList<String[]> stringArrayList = new ArrayList<String[]>();
        //instantiate a picklist
        for (Document d : documentList){
            stringArrayList.add(this.staticData.getPickListRow(d));
        }
        return stringArrayList;
    }
    public List<AppError> fieldValidate(String content){
        return this.staticData.fieldValidate(content);
    }

}

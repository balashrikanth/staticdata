package com.stonex.corp.payments.staticdata.entity;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stonex.corp.payments.staticdata.model.ChangeInfo;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staticdata-audit")
public class StaticDataAuditDB {
    @Id
    private String _id;
    private int version;
    private String staticDataPK;
    private String functionId;
    private String collectionName;
    private String jsonValue;//This will either old or new
    private String oldcontent;
    private String newcontent;

    public StaticDataAuditDB(String functionId, String oldcontent, String newcontent, int version){
        this.version = version;
        this.functionId = functionId;
        String jsonContent = null;
        if (oldcontent!=null){
            if (!oldcontent.equalsIgnoreCase("")){
                this.oldcontent = oldcontent;
                StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,oldcontent);
                this.staticDataPK = staticDataFactory.getPKValue();
                this.collectionName = staticDataFactory.getCollectionName();
                this.jsonValue = jsonContent;
            }
        }
        if (newcontent!=null){
            if (!newcontent.equalsIgnoreCase("")){
                this.newcontent = newcontent;
                StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,newcontent);
                this.staticDataPK = staticDataFactory.getPKValue();
                this.collectionName = staticDataFactory.getCollectionName();
                this.jsonValue = jsonContent;
            }
        }
    }

    public List<ChangeInfo> getChangeInfoOld(String[] labels){
        List<ChangeInfo> changeInfoList = new ArrayList<>();

        for (String s : labels){
            ChangeInfo changeInfo = new ChangeInfo();
            changeInfo.setFieldName(s);
            if (this.oldcontent!=null){
                JSONObject jsonObject = new JSONObject((this.oldcontent));
                changeInfo.setOldValue(jsonObject.get(s).toString());
            }
            if (this.newcontent!=null){
                JSONObject jsonObject = new JSONObject((this.newcontent));
                changeInfo.setNewValue(jsonObject.get(s).toString());
            }
            if (changeInfo.getOldValue()!=null && changeInfo.getNewValue()!=null){
                if (!changeInfo.getOldValue().trim().equalsIgnoreCase(changeInfo.getNewValue().trim())){
                    changeInfo.setDifference(true);
                }
            }

            changeInfoList.add(changeInfo);
        }
        return changeInfoList;
    }

    public List<ChangeInfo> getChangeInfo(String[] labels){
        List<ChangeInfo> changeInfoList = new ArrayList<>();
        for (String s : labels){
            ChangeInfo changeInfo = new ChangeInfo();
            changeInfo.setFieldName(s);
            if (this.oldcontent!=null){
                changeInfo.setOldValue(getRecursiveElement(this.oldcontent,s));

            }
            if (this.newcontent!=null){
                changeInfo.setNewValue(getRecursiveElement(this.newcontent,s));

            }
            if (changeInfo.getOldValue()!=null && changeInfo.getNewValue()!=null){
                if (!changeInfo.getOldValue().trim().equalsIgnoreCase(changeInfo.getNewValue().trim())){
                    changeInfo.setDifference(true);
                }
            }
            changeInfoList.add(changeInfo);
        }
        return changeInfoList;
    }

    public String getRecursiveElement(String labelContent, String label) {
        String returnValue="";
        try {
            Gson gson = new GsonBuilder().create();
            JsonObject gsonJsonObject = gson.fromJson(labelContent, JsonObject.class);
            int elementCount = StringUtils.countMatches(label, ".");
            int arrayCount = StringUtils.countMatches(label,"[]");
            JsonObject partialobject = new JsonObject();
            if (elementCount>0 && arrayCount>0 ){
                    //If both inner element and Array is found and the last one is array
                    String[] labelSets = label.split("\\.");
                    for (int i=0;i<=elementCount;i++){
                        if (i==elementCount){
                            String splitlabel = labelSets[i].split("\\[")[0];
                            returnValue = gsonJsonObject.getAsJsonArray(splitlabel).toString();
                        } else {
                            partialobject = gsonJsonObject.getAsJsonObject(labelSets[i]);
                        }
                    }
            } else if (elementCount > 0) {
                //If only inner element is found
                String[] labelSets = label.split("\\.");
                for (int i=0;i<=elementCount;i++){
                    if (i==elementCount){
                        returnValue = partialobject.get(labelSets[i]).getAsString();
                    } else {
                        partialobject = gsonJsonObject.getAsJsonObject(labelSets[i]);
                    }
                }
            } else if (arrayCount>0){
                //If only array found - iterate through all items in array
                String splitlabel = label.split("\\[")[0];
                returnValue = "";
                for (int i=0;i<gsonJsonObject.getAsJsonArray(splitlabel).size();i++){
                    returnValue = returnValue.concat(gsonJsonObject.getAsJsonArray(splitlabel).get(i).getAsJsonObject().toString());
                }
            }
            else {
                //simple element
                returnValue = gsonJsonObject.get(label).getAsString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  returnValue;
    }



}

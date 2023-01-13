package com.stonex.corp.payments.staticdata.entity;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.domain.Country;
import com.stonex.corp.payments.staticdata.model.ChangeInfo;
import com.stonex.corp.payments.staticdata.model.StaticData;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
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

    public List<ChangeInfo> getChangeInfo(String[] labels){
        List<ChangeInfo> changeInfoList = new ArrayList<ChangeInfo>();

        for (String s : labels){
            ChangeInfo changeInfo = new ChangeInfo();
            changeInfo.setFieldName(s);
            if (this.oldcontent!=null){
                JSONObject jsonObject = new JSONObject((this.oldcontent));
                changeInfo.setOldValue(jsonObject.getString(s));
            }
            if (this.newcontent!=null){
                JSONObject jsonObject = new JSONObject((this.newcontent));
                changeInfo.setNewValue(jsonObject.get(s).toString());
            }
            if (changeInfo.getOldValue()!=null && changeInfo.getNewValue()!=null){
                if (!changeInfo.getOldValue().equalsIgnoreCase(changeInfo.getNewValue())){
                    changeInfo.setDifference(true);
                }
            }

            changeInfoList.add(changeInfo);
        }
        return changeInfoList;
    }



}

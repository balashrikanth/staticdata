package com.stonex.corp.payments.staticdata.entity;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Audit;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staticdata-meta")
public class StaticDataMetaInfoDB {
    @Id
    private String _id;
    private String staticDataPK;
    private String collectionName;
    private String className;
    private boolean approved;
    private Audit lastAudit;
    private List<Audit> auditInfoList =new ArrayList<>();
    private String status;
    private int version;

    public StaticDataMetaInfoDB(String jsonContent, String functionId, String userId, String action){
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        this.staticDataPK = staticDataFactory.getPKValue();
        this.collectionName = staticDataFactory.getCollectionName();
        this.className = staticDataFactory.getClassName();
        this.approved = false;//Initial Status is always false
        this.lastAudit = new Audit(userId, action);
        this.auditInfoList = new ArrayList<Audit>();
        this.auditInfoList.add(this.lastAudit);
        this.status=SystemFieldConfig.ACTIONNEW;//First Time NEW

    }
    public void update(String userId,String action, String approveRemark ){
        switch(action.toUpperCase()) {
            case SystemFieldConfig.ACTIONAPPROVE:
                this.lastAudit.addApprover(userId,approveRemark);
                this.approved=true;
                break;
            default:
                this.lastAudit.addUser(userId,action);
                this.approved=false;
                break;
        }
        this.auditInfoList.add(this.lastAudit);
        this.status=action;
    }

}

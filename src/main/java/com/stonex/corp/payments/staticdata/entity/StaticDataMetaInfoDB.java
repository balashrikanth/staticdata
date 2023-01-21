package com.stonex.corp.payments.staticdata.entity;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Audit;
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
    private boolean approved;
    private Audit lastAudit;
    private List<Audit> auditInfoList =new ArrayList<>();
    private String status;
    private int version;

    public StaticDataMetaInfoDB(String staticDataPK, String collectionName, String userId, String action){
        this.staticDataPK = staticDataPK;
        this.collectionName = collectionName;
        this.approved = false;//Initial Status is always false
        this.version=1;
        this.lastAudit = new Audit(userId, action);
        this.lastAudit.setVersion(this.version);
        this.auditInfoList = new ArrayList<Audit>();
        this.auditInfoList.add(this.lastAudit);
        this.status=SystemFieldConfig.ACTIONNEW;//First Time NEW
    }
    public void update(String userId,String action, String approveRemark ){
        this.version++;
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
        this.lastAudit.setVersion(this.version);
        this.auditInfoList.add(this.lastAudit);
        this.status=action;
    }

}

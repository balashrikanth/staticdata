package com.stonex.corp.payments.staticdata.model;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    private String creatorId;
    private long creationDate;
    private String approverId;
    private long approvedDate;
    private String approveRemark;
    private String action; // Create / Update/Modify ..
    private int version;//this relates to

    public Audit(String userId, String action){
        this.creatorId = userId;
        this.action = action;
        this.creationDate = Instant.now().getEpochSecond();
        this.approverId="-";//Clear Approver Information
        this.approvedDate=0;
        this.approveRemark = "-";
    }
    public void addUser(String userId,String action){
        this.action = action;
        this.creatorId = userId;
        this.creationDate = Instant.now().getEpochSecond();
        this.approverId="";//Clear Approver Information
        this.approvedDate=0;
        this.approveRemark = "-";

    }

    public void addApprover(String userId,String remark){
        this.approverId = userId;
        this.approveRemark = remark;
        this.action = SystemFieldConfig.ACTIONAPPROVE;
        this.approvedDate = Instant.now().getEpochSecond();
    }
}

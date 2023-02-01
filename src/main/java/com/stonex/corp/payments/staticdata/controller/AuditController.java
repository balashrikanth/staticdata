package com.stonex.corp.payments.staticdata.controller;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.dal.StaticDataDAL;
import com.stonex.corp.payments.staticdata.dto.AppReturnObject;
import com.stonex.corp.payments.staticdata.entity.StaticDataAuditDB;
import com.stonex.corp.payments.staticdata.entity.StaticDataMetaInfoDB;

import com.stonex.corp.payments.staticdata.model.Audit;
import com.stonex.corp.payments.staticdata.model.ChangeInfo;
import com.stonex.corp.payments.staticdata.repository.StaticDataAuditDBRepository;
import com.stonex.corp.payments.staticdata.repository.StaticDataMetaInfoDBRepository;
import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/audit")
public class AuditController {
    @Autowired
    StaticDataDAL staticDataDAL;
    @Autowired
    StaticDataMetaInfoDBRepository staticDataMetaInfoDBRepository;
    @Autowired
    StaticDataAuditDBRepository staticDataAuditDBRepository;


    @PostMapping("/record")
    public String getAuditforRecord(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId, @RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        StaticDataMetaInfoDB staticDataMetaInfoDB = this.staticDataMetaInfoDBRepository.findFirstByStaticDataPKAndCollectionName(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName());
        if (staticDataMetaInfoDB!=null){
            List<Audit> auditList = staticDataMetaInfoDB.getAuditInfoList();
            if (auditList!=null){
                appReturnObject.PerformReturnObject(auditList);
            }
        }
        return appReturnObject.setReturnJSON();
    }

    @PostMapping("/change/version/{version}")
    public String getChangeForAuditRecord(@RequestHeader("functionId") String functionId, @RequestHeader("applicationId") String applicationId,@RequestHeader(value = "userid", defaultValue = SystemFieldConfig.SYSTEMUSER) String userId,@PathVariable("version") int version, @RequestBody String jsonContent){
        AppReturnObject appReturnObject = new AppReturnObject();
        StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,jsonContent);
        StaticDataAuditDB staticDataAuditDB = this.staticDataAuditDBRepository.findFirstByStaticDataPKAndCollectionNameAndVersion(staticDataFactory.getPKValue(),staticDataFactory.getCollectionName(),version);
        if (staticDataAuditDB!=null){
            List<ChangeInfo> changeInfoList = staticDataAuditDB.getChangeInfo(staticDataFactory.getLabels());
            if (changeInfoList!=null){
                appReturnObject.PerformReturnObject(changeInfoList);
            }
        }
        return appReturnObject.setReturnJSON();
    }
}

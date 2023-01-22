package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticDataWithAudit {
    private StaticData staticData;
    private Audit audit;

    public StaticDataWithAudit(StaticData staticData){
        this.staticData = staticData;
        this.audit = new Audit();
    }
}

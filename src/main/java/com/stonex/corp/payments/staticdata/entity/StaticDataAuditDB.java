package com.stonex.corp.payments.staticdata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staticdata-audit")
public class StaticDataAuditDB {
    @Id
    private String _id;
    private int version;
    private String staticDataPK;
    private String collectionName;
    private String oldcontent;
    private String newcontent;

}

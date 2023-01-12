package com.stonex.corp.payments.staticdata.entity;


import com.stonex.corp.payments.staticdata.utils.StaticDataFactory;
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

    public StaticDataAuditDB(String functionId, String oldcontent, String newcontent, int version){
        this.version = version;
        String jsonContent = null;
        if (oldcontent!=null){
            if (!oldcontent.equalsIgnoreCase("")){
                this.oldcontent = oldcontent;
                StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,oldcontent);
                this.staticDataPK = staticDataFactory.getPKValue();
                this.collectionName = staticDataFactory.getCollectionName();
            }
        }
        if (newcontent!=null){
            if (!newcontent.equalsIgnoreCase("")){
                this.newcontent = newcontent;
                StaticDataFactory staticDataFactory = new StaticDataFactory(functionId,newcontent);
                this.staticDataPK = staticDataFactory.getPKValue();
                this.collectionName = staticDataFactory.getCollectionName();
            }
        }

    }

}

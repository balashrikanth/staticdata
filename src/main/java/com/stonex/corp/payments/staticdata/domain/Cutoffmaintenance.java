package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cutoffmaintenance extends StaticData {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Cutoffmaintenance.class);
    //KEEP ALL attributes in small case as reflection is used
    private String currencycode;
    private String entityid;
    private String localtimezone;
    private int spotdays;
    private int externalpaylegsettlementday;
    private String externalpaylegcutofftime;
    private int stonexpaylegsettlementday;
    private String stonexpaylegcutofftime;
    private int externalreceivelegsettlementday;
    private String externalreceivelegcutofftime;
    private int stonexreceivelegsettlementday;
    private String stonexreceivelegcutofftime;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.
    @JsonIgnore
    static final String currencycodetag = "currencycode";
    @JsonIgnore
    static final String entityidtag = "entityid";
    @JsonIgnore
    static final String localtimezonetag = "localtimezone";
    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+ "cutoffmaintenance";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Cutoffmaintenance cutoffmaintenance = objectMapper.readValue(content, Cutoffmaintenance.class);
            returnValue = cutoffmaintenance.getCurrencycode().concat(cutoffmaintenance.getEntityid());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.currencycode.toUpperCase().concat(this.entityid.toUpperCase());//Key always upper case
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Cutoffmaintenance getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Cutoffmaintenance cutoffmaintenance = new Cutoffmaintenance();
        try {
            cutoffmaintenance = objectMapper.readValue(document.toJson(), Cutoffmaintenance.class);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return cutoffmaintenance;
    }

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = currencycodetag;
        headers[1] = entityidtag;
        headers[2] = localtimezonetag;
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get(currencycodetag)!=null){
            picklistcols[0] = document.get(currencycodetag).toString();
        }
        if (document.get(entityidtag)!=null){
            picklistcols[1] = document.get(entityidtag).toString();
        }
        if (document.get(localtimezonetag)!=null){
            picklistcols[2] = document.get(localtimezonetag).toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        return new String[]{currencycodetag,entityidtag,localtimezonetag,"spotdays","externalpaylegsettlementday","externalpaylegcutofftime","stonexpaylegsettlementday","stonexpaylegcutofftime","externalreceivelegsettlementday","externalreceivelegcutofftime","stonexreceivelegsettlementday","stonexreceivelegcutofftime","active"};
    }


}

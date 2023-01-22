package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cutoffmaintenance extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String currencycode;
    private String entityid;
    private String localtimezone;
    private int externalpaylegsettlementday;
    private String externalpaylegcutofftime;
    private int stonexpaylegsettlementday;
    private String stonexpaylegcutofftime;
    private int externalreceivelegsettlementday;
    private String externalreceivelegcutofftime;
    private int stonexreceivelegsettlementday;
    private String stonexreceivelegcutofftime;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return cutoffmaintenance;
    }

    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = "currencycode";
        headers[1] = "entityid";
        headers[2] = "localtimezone";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get("currencycode")!=null){
            picklistcols[0] = document.get("currencycode").toString();
        }
        if (document.get("entityid")!=null){
            picklistcols[1] = document.get("entityid").toString();
        }
        if (document.get("localtimezone")!=null){
            picklistcols[2] = document.get("localtimezone").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        String [] stringList = new String[]{"currencycode","entityid","localtimezone","externalpaylegsettlementday","externalpaylegcutofftime","stonexpaylegsettlementday","stonexpaylegcutofftime","externalreceivelegsettlementday","externalreceivelegcutofftime","stonexreceivelegsettlementday","stonexreceivelegcutofftime","active"};
        return stringList;
    }


}

package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.XCurrencyFee;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clientfee extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String clientid;
    private String clientsellcurrencycode;
    private double samecurrencyfee;
    private List<XCurrencyFee> clientbuycurrencies;
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+ "clientfee";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Clientfee clientfee = objectMapper.readValue(content, Clientfee.class);
            returnValue = clientfee.getClientid().concat(getClientsellcurrencycode());
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.clientid.toUpperCase().concat(this.clientsellcurrencycode.toUpperCase());//Key always upper case
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Clientfee getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Clientfee clientfee = new Clientfee();
        try {
            clientfee = objectMapper.readValue(document.toJson(), Clientfee.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return clientfee;
    }
    //Implement this for Report view in Summary.

    @JsonIgnore
    @Override
    public String getReportTitle(){
        return "CLIENT FEE LIST";
    }
    //Implement this for Report view in Summary.
    @JsonIgnore
    @Override
    public String getReportHeader(){
        HashMap<String,String> headerMap= new HashMap<String,String>();
        headerMap.put("clientid","CLIENT ID");
        headerMap.put("clientsellcurrencycode","SELL CURRENCY");
        headerMap.put("samecurrencyfee","SAME CURRRENCY FEE");
        headerMap.put("clientbuycurrencies_currencycode","BUY CURRENCY");
        headerMap.put("clientbuycurrencies_exchangefee","EXCHANGE FEE");
        headerMap.put("active","ACTIVE");
        JSONObject jsonObject = new JSONObject(headerMap);
        return jsonObject.toString();
    }
    //Implement this for Report view in Summary.
    @JsonIgnore
    @Override
    public String getReportFooter(){
        return "** END OF REPORT **";
    }

    //Implement this for Report view in Summary.
    @JsonIgnore
    @Override
    public List<Document> getReportData(MongoCollection<Document> collection){
        List<Document> documentList = new ArrayList<Document>();
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$unwind",
                new Document("path", "$clientbuycurrencies"))));
        for (Document document1 : result){
            document1.remove("_id");
            document1.remove("staticDataPK");
            documentList.add(document1);
        }
        return documentList;
    }




    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(2);//As set below
        String [] headers = new String[2];
        headers[0] = "clientid";
        headers[1] = "clientsellcurrencycode";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA"};
        if (document.get("clientid")!=null){
            picklistcols[0] = document.get("clientid").toString();
        }
        if (document.get("clientsellcurrencycode")!=null){
            picklistcols[1] = document.get("clientsellcurrencycode").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        String [] stringList = new String[]{"clientid","clientsellcurrencycode","samecurrencyfee","clientbuycurrencies[]","active"};
        return stringList;
    }


}

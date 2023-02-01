package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import com.stonex.corp.payments.staticdata.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fxentity extends StaticData {
    //KEEP ALL attributes in small case as reflection is used
    private String entityid;
    private String fullname;
    private String displayname;
    private String supportemail;
    private String phonenumber;
    private Address address;
    private String[] products;
    private String[] allowedsellccy;
    private String[] allowedbuyccy;
    private String relationshiptype;
    private String masteragreementtype;
    private String payididentifier;
    private List<EntityLookupTemplate> notificationlist;
    private List<EntityLookupTemplate> emaillist;

    private boolean active;//keep this attribute naming unchanged as picklist uses this.
    @JsonIgnore
    static final String entityidtag = "entityid";
    @JsonIgnore
    static final String fullnametag = "fullname";
    @JsonIgnore
    static final String displaynametag = "displayname";
    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return SystemFieldConfig.ENTITYPREFIX+"fxentity";
    }
    //Implement this to form the primary key - also known as staticDataPK.
    @Override
    public String createPK(String content) {
        String returnValue ="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Fxentity fxentity = objectMapper.readValue(content, Fxentity.class);
            returnValue = fxentity.getEntityid();
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    //Implement this to identify which fields constitute primary key
    @Override
    public String getPK(){
        return this.entityid.toUpperCase();//Key always upper case
    }

    //Implement this if you have to fill up other fields from main fields - denormalize scenario
    @Override
    public void enrichFields(){
        ArrayList<String> addressLine = new ArrayList<>();
        switch(this.entityid.trim().toUpperCase()){
            case "SFL":
                payididentifier="01";//London
                break;
            case "SFG":
                payididentifier="02";//EU
                break;
            case "SPS":
                payididentifier="03";//Americas
                break;
            case "SFP":
                payididentifier="04";//Singapore
                break;
            default:
                payididentifier="99";//Default
                break;
        }
        if (payididentifier==null || payididentifier.equalsIgnoreCase("")){
            payididentifier="99";//Default value
        }
        if (address!=null){
            String line1="";//Floor Building
            if (address.getFloor()!=null){
               line1 = line1.concat(address.getFloor().concat(" "));//Extra space as separator between fields
            }
            if (address.getBuildingname()!=null){
                line1 = line1.concat(address.getBuildingname());
            }
            if (!line1.equalsIgnoreCase("")){
                addressLine.add(line1);
            }
            String line2="";//Building No Street
            if (address.getBuildingno()!=null){
                line2 = line2.concat(address.getBuildingno().concat(" "));
            }
            if (address.getStreet()!=null){
                line2 = line2.concat(address.getStreet());
            }
            if (!line2.equalsIgnoreCase("")){
                addressLine.add(line2);
            }
            String line3="";//City District State
            if (address.getCity()!=null){
                line3 = line3.concat(address.getCity().concat(" "));
            }
            if (address.getDistrict()!=null){
                line3 = line3.concat(address.getDistrict().concat(" "));
            }
            if (address.getState()!=null){
                line3 =  line3.concat(address.getState());
            }
            if (!line3.equalsIgnoreCase("")){
                addressLine.add(line3);
            }
            String line4="";//Country Postal Code
            if (address.getCountry()!=null){
                line4 = line4.concat(address.getCountry().concat(" "));
            }
            if (address.getPostalcode()!=null){
                line4 = line4.concat(address.getPostalcode());
            }
            if (!line4.equalsIgnoreCase("")){
                addressLine.add(line4);
            }
        }

        if (!addressLine.isEmpty()){
            this.address.setAddressline(addressLine);
        }
    }

    //Implement this for extracting the object from mongo collection
    @Override
    public Fxentity getObjectFromDocument(Document document){
        ObjectMapper objectMapper = new ObjectMapper();
        Fxentity fxentity = new Fxentity();
        try {
            fxentity = objectMapper.readValue(document.toJson(), Fxentity.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return fxentity;
    }


    //Implement this for picklist columns for this collection
    @Override
    public Picklist getPickListHeaders(){
        Picklist picklist = new Picklist();
        picklist.setNoOfCols(3);//As set below
        String [] headers = new String[3];
        headers[0] = entityidtag;
        headers[1] = displaynametag;
        headers[2] = fullnametag;
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get(entityidtag)!=null){
            picklistcols[0] = document.get(entityidtag).toString();
        }
        if (document.get(displaynametag)!=null){
            picklistcols[1] = document.get(displaynametag).toString();
        }
        if (document.get(fullnametag)!=null){
            picklistcols[2] = document.get(fullnametag).toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        return new String[]{entityidtag,fullnametag,displaynametag,"supportemail","phonenumber","address.buildingno","address.buildingname","address.floor","address.street","address.city","address.district","address.postalcode","address.state","address.country","products[]","allowedsellccy[]","allowedbuyccy[]","relationshiptype","masteragreementtype","messagetemplateidlist[]","active"};
    }


}

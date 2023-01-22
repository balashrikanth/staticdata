package com.stonex.corp.payments.staticdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stonex.corp.payments.staticdata.model.Address;
import com.stonex.corp.payments.staticdata.model.Picklist;
import com.stonex.corp.payments.staticdata.model.StaticData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;


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
    private boolean active;//keep this attribute naming unchanged as picklist uses this.

    //Implement this for which collection name is to be used
    @Override
    public String getCollectionName(){
        return "static-fxentity";
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
        return this.entityid;
    }

    //Implement this if you have to fill up other fields from main fields - denormalize scenario
    @Override
    public void enrichFields(){
        ArrayList<String> addressLine = new ArrayList<String>();
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

        if (addressLine.size()>0){
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
        headers[0] = "entityid";
        headers[1] = "displayname";
        headers[2] = "fullname";
        picklist.setPickListHeaders(headers);
        return picklist;
    }
    //Implement this for picklist values in grid
    @Override
    public String[] getPickListRow(Document document){
        String [] picklistcols = new String[]{"NA","NA","NA"};
        if (document.get("entityid")!=null){
            picklistcols[0] = document.get("entityid").toString();
        }
        if (document.get("displayname")!=null){
            picklistcols[1] = document.get("displayname").toString();
        }
        if (document.get("fullname")!=null){
            picklistcols[2] = document.get("fullname").toString();
        }
        return picklistcols;
    }

    //Implement this for audit trail- change info values to be show
    @Override
    @JsonIgnore
    public String[] getLabels(){
        String [] stringList = new String[]{"entityid","fullname","displayname","supportemail","phonenumber","address.buildingno","address.buildingname","address.floor","address.street","address.city","address.district","address.postalcode","address.state","address.country","products[]","allowedsellccy[]","allowedbuyccy[]","active"};
        return stringList;
    }


}

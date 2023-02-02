package com.stonex.corp.payments.staticdata.model;

import com.stonex.corp.payments.staticdata.config.SystemFieldConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class Picklist {
    private int noOfCols;
    private String[] pickListHeaders;
    private List<String[]> pickListRowList;

    public Picklist(){
        this.noOfCols = SystemFieldConfig.PICKLISTCOLS;
        this.pickListHeaders = new String[SystemFieldConfig.PICKLISTCOLS];
        this.pickListRowList = new ArrayList<>();
    }
    public Picklist(int noOfCols, String[] headerCols) {
        this.noOfCols = noOfCols;
        this.pickListHeaders = headerCols;
        this.pickListRowList = new ArrayList<>();
    }

    public void addRows(List<String[]> pickListRowList){
        this.pickListRowList = pickListRowList;
    }

    public void addSingleRow(String[] picklistRow){
        this.pickListRowList.add(picklistRow);
    }

}

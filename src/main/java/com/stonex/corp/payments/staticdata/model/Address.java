package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String buildingno;
    private String buildingname;
    private String floor;
    private String street;
    private String city;
    private String district;
    private String postalcode;
    private String state;
    private String country;
    private List<String> addressline;
}
package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPerson {
    private String nameprefix;
    private String firstname;
    private String lastname;
    private String namesuffix;
    private String displayname;
    private String phonenb;
    private String mobnb;
    private String faxnb;
    private Address address;
    private String emailaddress;
    private String jobtitle;
    private String responsibility;
    private String department;
    private boolean consentformarketing;
    private List<OtherContact> othercontactlist;
    private String preferredchanneltype;

}

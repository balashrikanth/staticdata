package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Owner {
    private String persontype;//Individual or Company
    private String nameprefix;
    private String firstname;
    private String lastname;
    private String namesuffix;
    private String displayname;
    private String phonenb;
    private String emailaddress;
    private Address address;

}

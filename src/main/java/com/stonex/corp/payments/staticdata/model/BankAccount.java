package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private String deliveryname;//Easy to identify name
    private String currency;
    private String iban;
    private String accountnumber;
    private Owner owner;
    private String accountnumbertype;
    private String reference;
    private String bicfi;//BIC Code - SWIFT
    private List<ClearingSystemId> clearingsystemidlist;

}

package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldAttributes {
    private String datatype;
    private String datatypeerrorcode;
    private boolean mandatory;
    private String mandatoryerrorcode;
    private String dataconstraint;
    private String dataconstrainterrorcode;
    private int minlength;
    private int maxlength;
    private String lengtherrorcode;
    private String[] listofvalues;
    private String listofvalueserrorcode;
}

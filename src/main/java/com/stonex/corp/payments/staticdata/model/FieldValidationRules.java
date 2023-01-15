package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidationRules {
    private String fieldid;
    private FieldAttributes fieldattributes;

}

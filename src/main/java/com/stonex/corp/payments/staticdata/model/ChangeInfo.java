package com.stonex.corp.payments.staticdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeInfo {
    private String fieldName;
    private String oldValue;
    private String newValue;
    private boolean difference;//default false

}

package com.stonex.corp.payments.staticdata.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorItem {
    private String code;
    private String target;
    private String message;
}

package com.stonex.corp.payments.staticdata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCodeDB {
    @Id
    private String _id;
    private String language;
    private String errorCode;
    private String description;
}

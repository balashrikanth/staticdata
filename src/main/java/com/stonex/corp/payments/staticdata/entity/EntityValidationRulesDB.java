package com.stonex.corp.payments.staticdata.entity;

import com.stonex.corp.payments.staticdata.model.FieldValidationRules;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staticdata-validation")
public class EntityValidationRulesDB {
    private String functionid;
    private String allowduplicate;
    private String duplicateerrorcode;
    private List<FieldValidationRules> fieldvalidationrules;
}

package com.stonex.corp.payments.staticdata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entitytemplate")
public class TemplateDB {
    @Id
    private String _id;
    private String templatetype;
    private String key;
    private boolean active;
}

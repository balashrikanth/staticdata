package com.stonex.corp.payments.staticdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportView {
    private String title;
    private String header;
    private String body;
    private String footer;
}

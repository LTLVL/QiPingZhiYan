package com.zju.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Review {
    private Integer id;
    private Integer CompanyID;
    private String ReviewerName;
    private LocalDate ReviewTime;
    private String ReviewContent;
    private Float Rating;
    private String ReviewSource;
}

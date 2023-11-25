package com.zju.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Review {
    private Integer id;
    private Integer companyId;
    private String reviewerName;
    private LocalDate reviewTime;
    private String reviewContent;
    private Float rating;
    private String reviewSource;
}

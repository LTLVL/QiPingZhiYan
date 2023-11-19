package com.zju.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Review {
    private Integer Review_ID;
    private Integer Company_ID;
    private String Reviewer_Name;
    private LocalDate Review_Time;
    private String Review_Content;
    private float Rating;
    private String Review_Source;
}

package com.zju.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Review {
    private Integer id;
    private Integer companyId;
    private String reviewerName;
    private LocalDate reviewTime;
    private String reviewContent;
    private Float rating;
    private String reviewSource;
}

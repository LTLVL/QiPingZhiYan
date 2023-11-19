package com.zju.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CompanyAndReview {
    private Company company;
    private ArrayList<Review> reviews;
}

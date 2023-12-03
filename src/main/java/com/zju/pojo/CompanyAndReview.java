package com.zju.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAndReview {
    private Company company;
    private ArrayList<Review> reviews;
}

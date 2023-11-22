package com.zju.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zju.common.Response;
import com.zju.pojo.Company;
import com.zju.pojo.CompanyAndReview;
import com.zju.pojo.Review;
import com.zju.service.CompanyService;
import com.zju.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/subjective")
    public Response<List<CompanyAndReview>> selectAllSub() {
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Company::getSubjectiveRating);
        List<Company> companies = companyService.list(queryWrapper);
        ArrayList<CompanyAndReview> result = new ArrayList<>();
        companies.forEach(company -> {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyID, company.getId());
            List<Review> reviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) reviews);
        });
        return Response.success(result);
    }

    @GetMapping("/objective")
    public Response<List<CompanyAndReview>> selectAllOb() {
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Company::getObjectiveRating);
        List<Company> companies = companyService.list(queryWrapper);
        ArrayList<CompanyAndReview> result = new ArrayList<>();
        companies.forEach(company -> {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyID, company.getId());
            List<Review> reviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) reviews);
        });
        return Response.success(result);
    }

    @GetMapping("/{name}")
    public Response<CompanyAndReview> selectByName(@PathVariable String name) {
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Company::getCompanyName, name);
        Company company = companyService.getOne(queryWrapper);
        if (company == null) {
            //todo:调用爬虫查询公司数据
            return Response.error("没有此公司");
        }
        CompanyAndReview companyAndReview = new CompanyAndReview();
        companyAndReview.setCompany(company);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getCompanyID, company.getId());
        List<Review> reviews = reviewService.list(wrapper);
        companyAndReview.setReviews((ArrayList<Review>) reviews);
        return Response.success(companyAndReview);
    }
}

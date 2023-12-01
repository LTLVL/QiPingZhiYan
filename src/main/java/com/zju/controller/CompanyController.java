package com.zju.controller;

import com.zju.common.Response;
import com.zju.pojo.CompanyAndReview;
import com.zju.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CompanyController {
    @Autowired
    private CompanyService companyService;


    @GetMapping("/subjective")
    public Response<List<CompanyAndReview>> selectAllSub() {
        return companyService.selectAllSub();
    }

    @GetMapping("/objective")
    public Response<List<CompanyAndReview>> selectAllOb() {
        return companyService.selectAllOb();
    }

    /**
     * 根据公司名查询相关索引
     * @param companyName 企业名
     * @return {@link Response}<{@link CompanyAndReview}>
     */
    @GetMapping("/company/{companyName}")
    public Response<List<CompanyAndReview>> selectByName(@PathVariable String companyName) throws IOException {
        return companyService.selectByName(companyName);
    }

    /**
     * 根据评论查询相关企业
     * @param review 企业名
     * @return {@link Response}<{@link CompanyAndReview}>
     */
    @GetMapping("/review/{review}")
    public Response<List<CompanyAndReview>> selectByReview(@PathVariable String review) throws IOException{
        return companyService.selectByReview(review);
    }


}

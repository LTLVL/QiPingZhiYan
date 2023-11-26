package com.zju.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zju.common.Response;
import com.zju.pojo.Company;
import com.zju.pojo.CompanyAndReview;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

public interface CompanyService extends IService<Company> {
    Response<List<CompanyAndReview>> selectAllSub();
    Response<List<CompanyAndReview>> selectAllOb();
    Response<List<CompanyAndReview>> selectByName(@PathVariable String companyName) throws IOException;
    Response<List<CompanyAndReview>> selectByReview(@PathVariable String review) throws IOException;
}

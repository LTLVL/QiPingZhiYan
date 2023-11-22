package com.zju.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.mapper.CompanyMapper;
import com.zju.pojo.Company;
import com.zju.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}

package com.zju.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.mapper.CompanyMapper;
import com.zju.mapper.ReviewMapper;
import com.zju.pojo.Company;
import com.zju.pojo.Review;
import com.zju.service.CompanyService;
import com.zju.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
}

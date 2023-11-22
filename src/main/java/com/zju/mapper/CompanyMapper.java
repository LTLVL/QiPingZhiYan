package com.zju.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zju.pojo.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}

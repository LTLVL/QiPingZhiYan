package com.zju.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.zju.pojo.Data;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String code;
    private Data data;
    private String desc;
    private String sid;
}

package com.zju.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> Response<T> success(T object) {
        Response<T> r = new Response<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Response<T> error(String msg) {
        Response r = new Response();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Response<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}

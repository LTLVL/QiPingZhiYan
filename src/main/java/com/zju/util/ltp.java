package com.zju.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class ltp {
	// webapi接口地址
	private static final String WEBTTS_URL = "http://ltpapi.xfyun.cn/v2/sa";
	// 应用ID
	private static final String APPID = "9b2eaeb0";
	// 接口密钥
	private static final String API_KEY = "e4a1b3b5e2e56ac9291f0acb8e15a710";
	// 文本
	private static String TEXT = "公司评论样例";
	//情感分析固定为dependent
	private static final String TYPE = "dependent";

	private static void setText(String commentContext){
		TEXT = commentContext;
	}

	public ltp(String commentContext){
		this.TEXT=commentContext;
	}
	
	public static String itp(String commentContext) throws IOException {
		setText(commentContext);
		System.out.println(TEXT.length());
		Map<String, String> header = buildHttpHeader();
		String result = HttpUtil.doPost1(WEBTTS_URL, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		System.out.println("itp 接口调用结果：" + result);
		return result;
	}

	/**
	 * 组装http请求头
	 */
	private static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
		String curTime = System.currentTimeMillis() / 1000L + "";
		String param = "{\"type\":\"" + TYPE +"\"}";
		String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
		String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		header.put("X-Param", paramBase64);
		header.put("X-CurTime", curTime);
		header.put("X-CheckSum", checkSum);
		header.put("X-Appid", APPID);
		return header;
	}
}

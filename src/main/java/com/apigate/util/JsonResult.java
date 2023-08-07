package com.apigate.util;

import com.apigate.constant.ApigateRetCode;

import cn.hutool.json.JSONObject;

/**
 * 
 * @Description： 响应结果
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午3:51:51]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public class JsonResult extends JSONObject {

	private static final long serialVersionUID = 1L;

	public JsonResult() {
		this.set("code", ApigateRetCode.SUCCESS.getCode());
		this.set("msg", ApigateRetCode.SUCCESS.getMsg());
	}

	public JsonResult(int code, String msg) {
		this.set("code", code);
		this.set("msg", msg);
	}

	public JsonResult(Object data) {
		this.set("code", ApigateRetCode.SUCCESS.getCode());
		this.set("msg", ApigateRetCode.SUCCESS.getMsg());
		this.set("data", data);
	}

	public static JsonResult success() {
		return new JsonResult();
	}

	public static JsonResult success(Object data) {
		return new JsonResult(data);
	}

	public static JsonResult error(int code, String msg) {
		return new JsonResult(code, msg);
	}

	public static JsonResult error(ApigateRetCode retCode) {
		return new JsonResult(retCode.getCode(), retCode.getMsg());
	}

}

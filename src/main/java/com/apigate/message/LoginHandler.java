package com.apigate.message;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.apigate.constant.Const;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 
 * @Description： 登录指令处理
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月1日下午3:59:46]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Component
@Configuration
public class LoginHandler implements C2SMessageHandler {

	@Override
	public JSON handlerMessage(StringBuilder iemiBuilder, JSONObject jsonData) {
		String phone = "";
		String smsCode = "";

		log.debug("LoginHandler Message，请求参数 = {}", jsonData.toString());
		if (jsonData.containsKey("phone")) {
			phone = jsonData.getStr("phone");
		}
		if (jsonData.containsKey("smsCode")) {
			smsCode = jsonData.getStr("smsCode");
		}

		JSON retObj = JSONUtil.createObj().set("code", -1);
		//
		JSONObject obj = JSONUtil.parseObj(retObj);
		if (obj.containsKey("accessToken") && obj.getInt("code") == Const.ERROR_CODE_SUCCESS) {

			iemiBuilder.delete(0, iemiBuilder.length());
			// 关联IEMI
			iemiBuilder.append(phone);
		}
		log.debug("phone = {}, smsCode = {}, LoginHandler Message，处理完毕，回复  = {}", phone, smsCode, retObj.toString());
		return retObj;
	}

}

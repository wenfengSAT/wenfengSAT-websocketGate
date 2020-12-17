package com.apigate.message;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.apigate.constant.Const;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
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
		String userId = "";
		String password = "";
		String iemi = "";
		String pushToken = "";
		log.debug("C2SLoginMessage，请求参数 = {}", jsonData.toString());
		if (jsonData.containsKey("userId")) {
			userId = jsonData.getStr("userId");
		}
		if (jsonData.containsKey("password")) {
			password = jsonData.getStr("password");
		}
		if (jsonData.containsKey("iemi")) {
			iemi = jsonData.getStr("iemi");
		}
		if (jsonData.containsKey("pushToken")) {
			pushToken = jsonData.getStr("pushToken");
		}
		JSON retObj = JSONUtil.createObj().set("code", 0).set("accessToken", RandomUtil.simpleUUID());
		//
		JSONObject obj = JSONUtil.parseObj(retObj);
		if (obj.containsKey("accessToken") && obj.getInt("code") == Const.ERROR_CODE_SUCCESS) {
			log.debug("accessToken = {}, accessToken = {}, SessionId = {}", userId, obj.getStr("accessToken"),
					jsonData.getStr("SessionId", StrUtil.EMPTY));
			iemiBuilder.delete(0, iemiBuilder.length());
			// 关联IEMI
			iemiBuilder.append(userId);
		}
		log.debug("UserId = {}, C2SLoginMessage，处理完毕，回复  = {}", userId, retObj.toString());
		return retObj;
	}

}

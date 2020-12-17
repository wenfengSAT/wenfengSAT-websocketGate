package com.apigate.message;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;

/**
 * 
 * @Description： 退出登录指令处理
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月2日上午9:39:05]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Component
@Configuration
public class LogoutHandler implements C2SMessageHandler {

	@Override
	public JSON handlerMessage(StringBuilder deviceId, JSONObject jsonData) {
		log.debug("开始处理LoginOut请求，参数={}", jsonData.toString());
		JSONObject retObj = new JSONObject();
		retObj.set("code", 0);
		retObj.set("msg", "success");
		return retObj;
	}

}

package com.apigate.message.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import com.apigate.constant.ApigateRetCode;
import com.apigate.message.C2SMessageHandler;
import com.apigate.util.JsonResult;
import com.apigate.ws.WebSocketServer;

import cn.hutool.core.util.IdUtil;
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

	public JsonResult handlerMessage(String processCode, Session session, JSONObject req) {
		String uid = req.getStr("uid");
		if (StrUtil.isBlank(uid)) {
			log.error("【processCode:{}】 uid is null!",processCode);
			return JsonResult.error(ApigateRetCode.ERROR_PARAM);
		}
		WebSocketServer.getWebSocketSessionMap().put(uid, session);
		JSON data = JSONUtil.createObj().set("accessToken", IdUtil.simpleUUID());
		return JsonResult.success(data);
	}

}

package com.apigate.message.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import com.apigate.constant.ApigateRetCode;
import com.apigate.message.C2SMessageHandler;
import com.apigate.util.JsonResult;
import com.apigate.ws.WebSocketServer;

import cn.hutool.core.util.StrUtil;
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
	public JsonResult handlerMessage(String processCode, Session session, JSONObject req) {
		String phone = req.getStr("phone");
		if (StrUtil.isBlank(phone)) {
			return JsonResult.error(ApigateRetCode.ERROR_PARAM);
		}
		WebSocketServer.getWebSocketSessionMap().remove(phone);
		return JsonResult.success();
	}

}

package com.apigate.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.apigate.config.RedisChannelConfig;
import com.apigate.constant.ApigateRetCode;
import com.apigate.model.SocketMessage;
import com.apigate.service.MessageService;
import com.apigate.util.JsonResult;
import com.apigate.ws.WebSocketServer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * 
 * @Description： 消息处理接口(服务端->客户端)
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午4:00:34]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final StringRedisTemplate stringRedisTemplate;

	/**
	 * 
	 * @Description： 根据用户ID下发消息-集群
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年10月7日下午2:32:06]
	 * @param uid
	 * @param message
	 * @return
	 *
	 */
	@Override
	public JsonResult pushClusterMessage(SocketMessage message) {
		JSONObject msgBody = message.getMsgBody();
		if (JSONUtil.isNull(msgBody)) {
			return JsonResult.error(ApigateRetCode.MSG_BODY_NULL);
		}
		String msgId = message.getMsgId();
		String uid = message.getUid();
		if (StrUtil.hasBlank(msgId, uid)) {
			return JsonResult.error(ApigateRetCode.ERROR_PARAM);
		}
		msgBody.set("msgId", msgId);
		msgBody.set("uid", uid);
		stringRedisTemplate.convertAndSend(RedisChannelConfig.topic, JSONUtil.toJsonStr(msgBody));
		return JsonResult.success();
	}

	/**
	 * 
	 * @Description： 根据用户ID下发消息-单机
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年10月7日下午2:36:51]
	 * @param uid
	 * @param message
	 * @return
	 *
	 */
	@Override
	public JsonResult pushSocketMessage(SocketMessage message) {
		JSONObject msgBody = message.getMsgBody();
		if (JSONUtil.isNull(msgBody)) {
			return JsonResult.error(ApigateRetCode.MSG_BODY_NULL);
		}
		String msgId = message.getMsgId();
		String uid = message.getUid();
		if (StrUtil.hasBlank(msgId, uid)) {
			return JsonResult.error(ApigateRetCode.ERROR_PARAM);
		}
		msgBody.set("msgId", msgId);
		msgBody.set("uid", uid);
		return WebSocketServer.sendMsgByUserId(msgBody, uid);
	}

}

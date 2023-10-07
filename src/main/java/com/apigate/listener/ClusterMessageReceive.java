package com.apigate.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.apigate.ws.WebSocketServer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
 * @Description： 集群消息推送监听
 * 
 * @author [ wenfengSAT@163.com ] on [2023年10月7日上午9:24:52]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Component
public class ClusterMessageReceive implements MessageListener {

	private static Log log = LogFactory.get();

	/**
	 * 
	 * @Description： 集群消息推送监听
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年10月7日下午12:05:00]
	 * @param msg
	 * @param pattern
	 * @return
	 *
	 */
	@Override
	public void onMessage(Message msg, byte[] pattern) {
		String body = new String(msg.getBody());
		String channel = new String(msg.getChannel());
		String patternStr = new String(pattern);// 如果是 ChannelTopic, 则 channel 字段与 pattern 字段值相同
		JSONObject message = JSONUtil.parseObj(body);
		String uid = message.getStr("uid");
		if (StrUtil.isBlank(uid)) {
			log.info("【channel:{}】【pattern:{}】clusterMessageReceive uid is null!", channel, patternStr, body);
			return;
		}
		log.info("【uid:{}】【channel:{}】【pattern:{}】clusterMessageReceive {}!", uid, channel, patternStr, body);
		WebSocketServer.sendMsgByUserId(message, uid);
	}

}

package com.apigate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.config.RedisChannelConfig;
import com.apigate.util.JsonResult;
import com.apigate.ws.WebSocketServer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 
 * @Description： 消息处理接口(服务端->客户端)
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午4:00:34]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@RestController
@RequestMapping("/api/msg")
public class MessageController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 
	 * @Description： 根据用户ID下发消息-集群
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年9月28日下午1:29:24]
	 * @param uid
	 * @param message
	 * @return
	 *
	 */
	@PostMapping("/cluster/push/{uid}")
	public JsonResult pushClusterhMessage(@PathVariable("uid") String uid, JSONObject message) {
		stringRedisTemplate.convertAndSend(RedisChannelConfig.topic, JSONUtil.toJsonStr(message));
		return JsonResult.success();
	}

	/**
	 * 
	 * @Description： 根据用户ID下发消息-单机
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午5:40:53]
	 * @param uid
	 * @param message
	 * @return
	 *
	 */
	@PostMapping("/socket/push/{uid}")
	public JsonResult pushMessage(@PathVariable("uid") String uid, JSONObject message) {
		return WebSocketServer.sendMsgByUserId(message, uid);
	}

	/**
	 * 
	 * @Description： 测试消息下发
	 * 
	 * MSG：{"processCode":"login","uid":"xxxxx"}
	 * 
	 * MSG：{"processCode":"logout","uid":"xxxxx"}
	 * 
	 * URL：localhost:8080/api/msg/test/push/xxxxx
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午4:59:09]
	 * @param uid
	 * @return
	 *
	 */
	@GetMapping("/test/push/{uid}")
	public JsonResult pushMessage(@PathVariable("uid") String uid) {
		JSONObject message = JSONUtil.createObj().set("uid", uid);
		return WebSocketServer.sendMsgByUserId(message, uid);
	}

	/**
	 * 
	 * @Description： 测试消息下发,把工程端口号修改,再启动一个服务
	 * 
	 * MSG：{"processCode":"login","uid":"xxxxx"}
	 * 
	 * MSG：{"processCode":"logout","uid":"xxxxx"}
	 * 
	 * URL：localhost:8081/api/msg/cluster/push/xxxxx
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午4:59:09]
	 * @param uid
	 * @return
	 *
	 */
	@GetMapping("/cluster/push/{uid}")
	public JsonResult pushClusterhMessage(@PathVariable("uid") String uid) {
		JSONObject message = JSONUtil.createObj().set("uid", uid);
		stringRedisTemplate.convertAndSend(RedisChannelConfig.topic, JSONUtil.toJsonStr(message));
		return JsonResult.success();
	}

	/**
	 * 
	 * @Description： 广播消息
	 * 
	 * URL：localhost:8080/api/msg/test/sendMsgToAnyone
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午5:39:03]
	 * @return
	 *
	 */
	@GetMapping("/test/sendMsgToAnyone")
	public JsonResult sendMsgToAnyone() {
		JSONObject message = JSONUtil.createObj().set("msg", "我给大家广播一条消息！");
		return WebSocketServer.sendMsgToAnyone(message);
	}

}

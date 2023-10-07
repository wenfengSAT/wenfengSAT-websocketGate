package com.apigate.ws;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.BeforeHandshake;
import org.yeauty.annotation.OnBinary;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnError;
import org.yeauty.annotation.OnEvent;
import org.yeauty.annotation.OnMessage;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.PathVariable;
import org.yeauty.annotation.RequestParam;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import com.apigate.constant.ApigateRetCode;
import com.apigate.constant.Command;
import com.apigate.constant.Const;
import com.apigate.message.C2SMessageHandler;
import com.apigate.util.JsonResult;
import com.apigate.util.SpringUtil;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * @Description： WebSocket服务
 * 
 * websocket服务地址：ws://127.0.0.1:9094/mobile/v1.0
 * 
 * websocket在线测试：http://coolaf.com/tool/chattest
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月1日下午6:46:42]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@ServerEndpoint(port = "${ws.port}", path = "/mobile/{version}", maxFramePayloadLength = "6553600")
public class WebSocketServer {

	private static Log log = LogFactory.get();

	// 保存已连接会话
	private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
	// 保存已登录会话
	private static ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
	// 客户端的连接会话
	private Session session;
	// 本次登录使用的IP
	private String localIP;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 
	 * @Description： 当有新的连接进入时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:03:29]
	 * @param session
	 * @param headers
	 * @param req
	 * @param reqMap
	 * @param arg
	 * @param pathMap
	 *
	 */
	@BeforeHandshake
	public void handshake(Session session, HttpHeaders headers, @RequestParam String req,
			@RequestParam MultiValueMap<String, Object> reqMap, @PathVariable String arg,
			@PathVariable Map<String, Object> pathMap) {
		session.setSubprotocols("stomp");
		log.info("【req:{}】【arg:{}】【reqMap:{}】【pathMap:{}】handshake!", session.id(), req, arg,
				JSONUtil.toJsonStr(reqMap), JSONUtil.toJsonStr(pathMap));
	}

	/**
	 * 
	 * @Description： 当有新的WebSocket连接完成时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:03:46]
	 * @param session
	 * @param headers
	 * @param req
	 * @param reqMap
	 * @param arg
	 * @param pathMap
	 *
	 */
	@OnOpen
	public void onOpen(Session session, HttpHeaders headers, @RequestParam String req,
			@RequestParam MultiValueMap<String, Object> reqMap, @PathVariable String arg,
			@PathVariable Map<String, Object> pathMap) {
		this.session = session;
		this.localIP = NetUtil.getLocalMacAddress();
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
	}

	/**
	 * 
	 * @Description： 当有WebSocket连接关闭时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:04:03]
	 * @param session
	 * @throws IOException
	 *
	 */
	@OnClose
	public void onClose(Session session) throws IOException {
		webSocketSet.remove(this); // 从set中删除
		removeLoginSession(session);// 删除掉线用户登录会话
		subOnlineCount(); // 在线数减1
		session.close();
	}

	/**
	 * 
	 * @Description： 当有WebSocket抛出异常时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:18:06]
	 * @param session
	 * @param cause
	 * @throws IOException
	 *
	 */
	@OnError
	public void onError(Session session, Throwable cause) throws IOException {
		log.error("【sessionId:{}】onError {}!", session.id(), cause.getMessage());
		if (Objects.nonNull(session) && session.isOpen()) {
			// session.close();
		}
	}

	/**
	 * 
	 * @Description： 当接收到二进制消息时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:18:18]
	 * @param session
	 * @param bytes
	 *
	 */
	@OnBinary
	public void onBinary(Session session, byte[] bytes) {
		String hexStr = HexUtil.encodeHexStr(bytes, false);
		log.info("【sessionId:{}】onBinary message:{}!", session.id(), hexStr);
		session.sendBinary(bytes);
	}

	/**
	 * 
	 * @Description： 当接收到字符串消息时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:36:19]
	 * @param session
	 * @param message
	 * @throws IOException
	 *
	 */
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		if (!JSONUtil.isTypeJSON(message)) {
			log.error("【message:{}】message is not json!", message);
			sendMessage(JsonResult.error(ApigateRetCode.MEDIATYPENOTSUPPORTED));
			return;
		}
		JSONObject req = JSONUtil.parseObj(message);
		if (!req.containsKey("processCode")) {
			log.error("【message:{}】processCode is null!", message);
			sendMessage(JsonResult.error(ApigateRetCode.PROCESS_CODE_ERROR));
			return;
		}
		String processCode = req.getStr("processCode");
		if (!Command.isTrueProcessCode(processCode)) {
			log.error("【message:{}】processCode is not exist!", message);
			sendMessage(JsonResult.error(ApigateRetCode.PROCESS_CODE_ERROR));
			return;
		}
		Class<?> service = Command.getService(processCode);
		C2SMessageHandler messageHandler = null;
		try {
			messageHandler = (C2SMessageHandler) SpringUtil.getBean(service);
		} catch (Exception e) {
			log.error("【processCode:{}】 get messageHandler bean fail!", processCode);
			sendMessage(JsonResult.error(ApigateRetCode.SYSTEM_EXCEPTION));
			return;
		}
		try {
			req.set("sessionId", session.id().asShortText());
			req.set("localIP", localIP);
			JsonResult result = messageHandler.handlerMessage(processCode, session, req);
			sendMessage(result);
		} catch (Exception e) {
			log.error("【processCode:{}】【message:{}】messageHandler error!", processCode, message);
			sendMessage(JsonResult.error(ApigateRetCode.SYSTEM_EXCEPTION));
			return;
		}
	}

	/**
	 * 
	 * @Description： 当接收到Netty的事件时，对该方法进行回调
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:36:30]
	 * @param session
	 * @param evt
	 *
	 */
	@OnEvent
	public void onEvent(Session session, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			switch (idleStateEvent.state()) {
			case READER_IDLE:
				log.info("【sessionId:{}】onEvent read idle!", session.id());
				break;
			case WRITER_IDLE:
				log.info("【sessionId:{}】onEvent write idle!", session.id());
				break;
			case ALL_IDLE:
				log.info("【sessionId:{}】onEvent all idle!", session.id());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @Description： 实现服务器主动推送
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:13:34]
	 * @param message
	 * @return
	 * @throws IOException
	 *
	 */
	public ChannelFuture sendMessage(JSONObject message) throws IOException {
		return this.session.sendText(message.toString());
	}

	/**
	 * 
	 * @Description： 广播自定义消息
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:36:47]
	 * @param message
	 * @param userId
	 *
	 */
	public static JsonResult sendMsgToAnyone(JSONObject message) {
		for (WebSocketServer item : webSocketSet) {
			try {
				item.sendMessage(message);
			} catch (IOException e) {
				log.error("【message:{}】sendMsgToAnyone error! {}", message, e);
				return JsonResult.error(ApigateRetCode.SYSTEM_EXCEPTION);
			}
		}
		return JsonResult.success();
	}

	/**
	 * 
	 * @Description： 根据用户ID发送消息
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午5:30:30]
	 * @param message
	 * @param userId
	 * @return
	 *
	 */
	public static JsonResult sendMsgByUserId(JSONObject message, String userId) {
		if (StrUtil.isBlank(userId)) {
			log.error("sendMsgByUserId fail, userId is null!");
			return JsonResult.error(ApigateRetCode.MSG_SEND_ERROR);
		}
		Session session = clients.get(userId);
		if (ObjectUtil.isNull(clients.get(userId))) {
			log.error("sendMsgByUserId fail, user is logout!");
			return JsonResult.error(ApigateRetCode.MSG_SEND_ERROR);
		}
		if (!webSocketSet.stream()
				.filter(webSocket -> webSocket.session.id().asShortText().equals(session.id().asShortText())).findAny()
				.isPresent()) {
			log.error("sendMsgByUserId fail, user is logout!");
			removeLoginSession(session);
			return JsonResult.error(ApigateRetCode.MSG_SEND_ERROR);
		}
		for (WebSocketServer item : webSocketSet) {
			try {
				if (item.session.id().asShortText().equals(session.id().asShortText())) {
					ChannelFuture future = item.sendMessage(message);
					future.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							log.debug("【userId:{}】sendMsgByUserId success", userId);
						}
					});
				}
			} catch (IOException e) {
				log.error("sendMsgByUserId error, {}", e);
				return JsonResult.error(ApigateRetCode.SYSTEM_EXCEPTION);
			}
		}
		return JsonResult.success();
	}

	/**
	 * 
	 * @Description： 在线用户+1
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:10:58]
	 *
	 */
	private void addOnlineCount() {
		stringRedisTemplate.opsForValue().increment(Const.onlineCountKey);
	}

	/**
	 * 
	 * @Description： 在线用户-1
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:11:21]
	 *
	 */
	private void subOnlineCount() {
		stringRedisTemplate.opsForValue().decrement(Const.onlineCountKey);
	}

	/**
	 * 
	 * @Description： 在线用户数
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午4:11:31]
	 * @return
	 *
	 */
	public long getOnlineCount() {
		String onlineCountValue = stringRedisTemplate.opsForValue().get(Const.onlineCountKey);
		if (StrUtil.isBlank(onlineCountValue) || !NumberUtil.isNumber(onlineCountValue)) {
			return 0l;
		}
		Long onlineCount = Long.parseLong(onlineCountValue);
		return onlineCount;

	}

	/**
	 * 
	 * @Description： 获取当前在线客户端对应的WebSocket对象
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午4:38:28]
	 * @return
	 *
	 */
	public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
		return webSocketSet;
	}

	/**
	 * 
	 * @Description： 获取当前在线客户端一登录用户的会话
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午4:49:17]
	 * @return
	 *
	 */
	public static ConcurrentHashMap<String, Session> getWebSocketSessionMap() {
		return clients;
	}

	/**
	 * 
	 * @Description： 删除掉线用户登录会话
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月7日下午2:58:01]
	 * @param session
	 *
	 */
	public static void removeLoginSession(Session session) {
		clients.entrySet().stream()
				.filter(client -> session.id().asLongText().equals(client.getValue().id().asLongText()))
				.forEach(client -> clients.remove(client.getKey()));
	}

	/**
	 * 
	 * @Description： 删除掉线用户登录会话
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月7日下午3:01:59]
	 * @param uid
	 *
	 */
	public static void removeLoginSession(String uid) {
		clients.remove(uid);
	}

}

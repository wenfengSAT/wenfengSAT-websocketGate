package com.apigate.ws;

import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnError;
import org.yeauty.annotation.OnMessage;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.PathVariable;
import org.yeauty.annotation.RequestParam;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import com.apigate.constant.Command;
import com.apigate.message.C2SMessageHandler;
import com.apigate.util.SpringUtil;

import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpHeaders;

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

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	// 本次登录使用的IP
	private String localIP;
	// 接收sid
	protected StringBuilder iemiBuilder = new StringBuilder();

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, HttpHeaders headers, @RequestParam String req,
			@RequestParam MultiValueMap<String, Object> reqMap, @PathVariable String arg,
			@PathVariable Map<String, Object> pathMap) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
		log.debug("UserId = {}, 通道ID={}, 当前连接人数={}", iemiBuilder.toString(), session.id().asShortText(),
				getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 *
	 * @throws IOException
	 */
	@OnClose
	public void onClose(Session session) throws IOException {
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		log.warn("UserId = {}, 通道ID = {}, 有一连接关闭！当前在线人数={}", this.iemiBuilder.toString(), session.id().asShortText(),
				getOnlineCount());
		iemiBuilder.delete(0, iemiBuilder.length());
		session.close();
	}

	/**
	 * 出错方法
	 *
	 * @throws IOException
	 */
	@OnError
	public void onError(Session session, Throwable cause) throws IOException {
		if (Objects.nonNull(this.session) && Objects.nonNull(cause) && !(cause instanceof EOFException)) {
			log.error("UserId = {}, 通道ID={}, 出错信息={}", iemiBuilder.toString(), this.session.id(), cause.toString());
		}
		if (Objects.nonNull(session) && session.isOpen()) {
			session.close();
		}
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 * @throws Exception
	 */
	@OnMessage
	public void onMessage(Session session, String message) {
		//
		JSONObject jsonData = JSONUtil.parseObj(message);
		if (!jsonData.containsKey("processCode")) {
			log.debug("UserId = {}, 通道ID={}, 上行内容={}, 上行请求非法，缺少command参数, 处理结束", iemiBuilder.toString(),
					session.id().asShortText(), message);
			return;
		}
		String processCode = jsonData.getStr("processCode");
		Class<?> service = Command.getService(processCode);
		//
		JSONObject logData = JSONUtil.parseObj(message);
		log.info("UserId = {}, 通道ID={}, 处理类={}, 开始处理，请求内容={}", iemiBuilder.toString(), session.id().asShortText(),
				service, logData.toString());
		// 通过command指令获取对应的消息处理类
		C2SMessageHandler aepMessage = null;
		try {
			aepMessage = (C2SMessageHandler) SpringUtil.getBean(service);
		} catch (Exception e) {
			log.error("UserId = {}, 通道ID = {}, 未找到协议头 = {} 的处理类", iemiBuilder.toString(), session.id().asShortText(),
					service);

			return;
		}
		// 回应客户端S2C
		try {
			localIP = NetUtil.getLocalMacAddress();
			// 保存本机IP
			jsonData.set("SessionId", session.id().asShortText());
			jsonData.set("LocalIP", localIP);
			JSON json = aepMessage.handlerMessage(iemiBuilder, jsonData);
			//
			jsonData = new JSONObject();
			jsonData.set("command", processCode);
			jsonData.set("data", json);
			String value = jsonData.toString();
			ChannelFuture future = sendMessage(value);
			log.info("UserId = {}, 通道ID = {}, 返回内容 = {}, future = {}, 处理结束", iemiBuilder.toString(),
					session.id().asShortText(), logData.toString(), future.toString());
		} catch (Exception e) {
			log.error("UserId = {}, 通道ID={}, 解析执行出错信息={}", iemiBuilder.toString(), session.id().asShortText(),
					e.getMessage());
		}
	}

	/**
	 * 群发、或单发自定义消息
	 */
	public static void sendInfo(JSONObject message, String userId) {
		for (WebSocketServer item : webSocketSet) {
			// 这里可以设定只推送给这个vin，为null则全部推送
			try {
				if (userId == null) {
					item.sendMessage(message.toString());
				} else if (item.iemiBuilder.toString().equals(userId)) {
					item.sendMessage(message.toString());
					log.debug("UserId = {}, 通道ID = {}, 下发成功，内容 = {}", userId, item.session.id().asShortText(),
							message.toString());
				}
			} catch (IOException e) {
				log.error("UserId = {}, 通道ID = {}, 下发失败 = {}", userId, item.session.id().asShortText(), e.getMessage());
			}
		}
	}

	/**
	 * 实现服务器主动推送
	 */
	public ChannelFuture sendMessage(String message) throws IOException {
		return this.session.sendText(message);
	}

	private void addOnlineCount() {

	}

	private void subOnlineCount() {

	}

	public Long getOnlineCount() {
		Long onlineCount = 0L;
		return onlineCount;
	}
}

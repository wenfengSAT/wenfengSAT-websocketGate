package com.apigate;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.apigate.constant.Command;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
 * @Description： 协议测试
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月2日上午9:57:57]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimulationTests {

	Log log = LogFactory.get();

	@Value("${ws.port}")
	int port;

	String requestVin = "TESTOPENVIN000011";

	static String token = "";

	@Test(timeout = 60 * 1000)
	public void testLogin() {

		JSONObject object = new JSONObject();
		object.set("UserId", 1);

		String url = "ws://127.0.0.1:" + port + "/mobile/v1";
		try {
			final WebSocketClient webSocketClient = new WebSocketClient(new URI(url), new Draft_6455()) {
				@Override
				public void onOpen(ServerHandshake handshakedata) {
					log.info("[websocket] 连接成功");
					//
					JSONObject jsonData = new JSONObject();
					jsonData.set("processCode", Command.login.getProcessCode());
					jsonData.set("phone", "15992486263");
					jsonData.set("smsCode", "xjj123456");
					jsonData.set("iemi", "iemi");
					jsonData.set("pushToken", "pushToken");
					this.send(jsonData.toString());
					ThreadUtil.sleep(2000);
					//
				}

				@Override
				public void onMessage(String message) {
					log.info("[websocket] 收到消息={}", message);
					JSONObject object = JSONUtil.parseObj(message);
					if (object.containsKey("accessToken")) {
						token = object.getStr("accessToken");
					}
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					log.info("[websocket] 退出连接");
				}

				@Override
				public void onError(Exception ex) {
					log.info("[websocket] 连接错误={}", ex.getMessage());
				}
			};
			//
			webSocketClient.connect();
			//
			ThreadUtil.sleep(10 * 1000);

			webSocketClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JSONObject jsonData = new JSONObject();
		jsonData.set("processCode", Command.login.getProcessCode());
		jsonData.set("phone", "15992486263");
		jsonData.set("smsCode", "xjj123456");
		jsonData.set("iemi", "iemi");
		jsonData.set("pushToken", "pushToken");
		System.out.println(jsonData.toString());
	}
}

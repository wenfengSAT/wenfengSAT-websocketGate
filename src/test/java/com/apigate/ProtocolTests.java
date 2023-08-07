package com.apigate;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.apigate.constant.Command;
import com.apigate.message.C2SMessageHandler;
import com.apigate.message.impl.LoginHandler;
import com.apigate.message.impl.LogoutHandler;
import com.apigate.util.SpringUtil;

import cn.hutool.json.JSONObject;

/**
 * 
 * @Description： 指令测试
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月2日上午9:58:49]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProtocolTests {

	@Test
	public void testProtocol() {
		//
		C2SMessageHandler msg = (LogoutHandler) SpringUtil.getBean(Command.logout.getService());
		JSONObject jsonData = new JSONObject();
		jsonData.set("accessToken", "accessToken");
		JSONObject retObj = (JSONObject) msg.handlerMessage(Command.logout.getProcessCode(), null, jsonData);
		assertTrue(retObj.getInt("code") == 0);
		//
		msg = (LoginHandler) SpringUtil.getBean(Command.login.getService());
		jsonData = new JSONObject();
		jsonData.set("accessToken", "accessToken");
		retObj = (JSONObject) msg.handlerMessage(Command.login.getProcessCode(), null, jsonData);
		assertTrue(retObj.getInt("code") == 0);
	}

}

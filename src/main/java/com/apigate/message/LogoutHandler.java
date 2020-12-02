package com.apigate.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.apigate.constant.Const;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

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


	@Autowired
	private RedisTemplate<String, String> redisTemplate;


	@Override
	public JSON handlerMessage(StringBuilder iemiBuilder, JSONObject jsonData) {
		log.debug("开始处理LoginOut请求，参数={}", jsonData.toString());
		String accessToken = jsonData.getStr("accessToken");
		// 先判断该用户的新旧token是否一致 如过不一样说明被挤掉
		String phone = redisTemplate.opsForValue().get(accessToken);
		if (StrUtil.isEmpty(phone)) {
			JSONObject retObj = new JSONObject();
			retObj.set("code", Const.ERROR_CODE_10001);
			retObj.set("msg", Const.ERROR_CODE_10001_DESP);
			log.debug("C2SLoginOutMessage回复  = {}", retObj.toString());
			return retObj;
		}
		JSON retObj = JSONUtil.createObj(); // mobileService.getSmSCode(phone, type);
		log.debug("phone = {}, LoginOut回复{}", phone, retObj.toString());
		return retObj;
	}

}

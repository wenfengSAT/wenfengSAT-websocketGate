package com.apigate.message;

import org.yeauty.pojo.Session;

import com.apigate.util.JsonResult;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
 * @Description： 消息处理接口(客户端->服务端)
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月1日下午1:42:58]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public interface C2SMessageHandler {

	Log log = LogFactory.get();

	/**
	 * 
	 * @Description： 消息处理方法(客户端->服务端)
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月2日下午5:03:23]
	 * @param processCode-请求编码
	 * @param session-会话
	 * @param req-请求参数
	 * @return
	 *
	 */
	JsonResult handlerMessage(String processCode, Session session, JSONObject req);

}

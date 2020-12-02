package com.apigate.message;

import cn.hutool.json.JSON;
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
	 * 处理类、处理方法
	 * 
	 * @param jsonData 手机端上传的数据
	 * @return 处理结果
	 */
	JSON handlerMessage(StringBuilder vin, JSONObject jsonData);

}

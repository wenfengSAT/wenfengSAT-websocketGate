package com.apigate.service;

import com.apigate.model.SocketMessage;
import com.apigate.util.JsonResult;

/**
 * 
 * @Description： 消息处理接口(服务端->客户端)
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午4:00:34]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public interface MessageService {

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
	JsonResult pushClusterMessage(SocketMessage message);

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
	JsonResult pushSocketMessage(SocketMessage message);
}

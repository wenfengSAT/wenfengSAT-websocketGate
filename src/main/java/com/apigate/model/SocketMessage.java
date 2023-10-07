package com.apigate.model;

import java.io.Serializable;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description： 消息实体
 * 
 * @author [ wenfengSAT@163.com ] on [2023年10月7日下午2:29:48]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Setter
@Getter
public class SocketMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgId;

	private String uid;

	private JSONObject msgBody;

}

package com.apigate.constant;


import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 
 * @Description： 统一异常错误码
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午3:49:38]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Getter
public enum ApigateRetCode implements CodeEnum {

	
	/**
	 * 成功
     */
	SUCCESS(200, "sucess!"),

    /**
      * 错误码
     */
    ERROR_PARAM(600, "请求参数不正确！"),
    REQUESTMETHODNOTSUPPORTED(601, "请求方式不支持！"),
    MEDIATYPENOTSUPPORTED(602, "请求协议格式不支持！"),
    SYSTEM_EXCEPTION(603, "系统错误！"),
    BUSY_SERVICE_EXCEPTION(604, "服务拥挤！"),
    SIGN_ERROR(605, "接口签名错误！"),
    PROCESS_CODE_ERROR(606, "指令错误错误！"),
    
    MSG_SEND_ERROR(700, "消息下发失败！"),
	;

	private int code;
	private String msg;

	ApigateRetCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getFormat(Object... str) {
		return StrUtil.format(this.getMsg(), str);
	}

}

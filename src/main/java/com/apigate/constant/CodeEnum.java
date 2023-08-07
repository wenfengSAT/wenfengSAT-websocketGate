package com.apigate.constant;

/**
 * 
 * @Description： 异常返回码枚举接口
 * 
 * @author [ wenfengSAT@163.com ] on [2023年8月1日下午3:47:31]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public interface CodeEnum {

	/**
	 * 
	 * @Description： 获取返回码
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午3:47:49]
	 * @return
	 *
	 */
	int getCode();

	/**
	 * 
	 * @Description： 获取返回信息
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年8月1日下午3:48:00]
	 * @return
	 *
	 */
	String getMsg();

}

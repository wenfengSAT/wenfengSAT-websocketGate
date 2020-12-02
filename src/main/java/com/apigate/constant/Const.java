package com.apigate.constant;

/**
 * 
 * @Description： 常量定义
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月2日上午9:36:39]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public interface Const {

	/**
	 * C2S标识定义
	 */
	public static final String C2S_DEF = "C2S.";
	/**
	 * S2C标识定义
	 */
	public static final String S2C_DEF = "S2C.";
	/**
	 * Bean标识定义
	 */
	public static final String BEAN_DEF = "Message";
	/////////////////////////////////////////////////////////////////////
	/**
	 * 获取AccessToken
	 */
	public static final String accessToken = "AccessToken";

	// 返回码
	public static final int ERROR_CODE_0 = 0;
	public static final int ERROR_CODE_SUCCESS = ERROR_CODE_0;
	public static final String ERROR_CODE_0_DESP = "操作成功";
	//
	public static final int ERROR_CODE_10001 = 10001;
	public static final String ERROR_CODE_10001_DESP = "手机请求参数不合法";

}

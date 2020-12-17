package com.apigate.constant;


import com.apigate.message.LoginHandler;
import com.apigate.message.LogoutHandler;

/**
 * 
 * @Description： 指令
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月1日下午4:37:18]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
public enum Command {

	login("login", LoginHandler.class, "登录接口"), 
	logout("logout", LogoutHandler.class, "登出接口"),;

	public String processCode;// 服务编码
	public Class<?> service;// 服务接口类
	public String desc;// 接口描述

	Command(String processCode, Class<?> service, String desc) {
		this.processCode = processCode;
		this.service = service;
		this.desc = desc;
	}

	public String getProcessCode() {
		return processCode;
	}

	public Class<?> getService() {
		return service;
	}

	public String getDesc() {
		return desc;
	}
	
	public static Command getCommand(String processCode) {
		for (Command command : Command.values()) {
			if (command.processCode.equals(processCode)) {
				return command;
			}
		}
		return null;
	}
	
	public static Class<?> getService(String processCode) {
		for (Command command : Command.values()) {
			if (command.processCode.equals(processCode)) {
				return command.getService();
			}
		}
		return null;
	}
	

}

package com.apigate.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yeauty.standard.ServerEndpointExporter;

/**
 * 
 * @Description： 开启WebSocket支持
 * 
 * @author [ wenfengSAT@163.com ] on [2020年12月2日上午9:37:56]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Configuration
public class WebSocketConfig {

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}

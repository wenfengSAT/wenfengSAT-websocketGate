package com.apigate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.apigate.listener.ClusterMessageReceive;

/**
 * 
 * @Description： 配置Redis消息监听器
 * 
 * @author [ wenfengSAT@163.com ] on [2023年9月28日下午1:44:02]
 * @Modified By： [修改人] on [修改日期] for [修改说明]
 *
 */
@Configuration
public class RedisChannelConfig {

	public static final String topic = "websocket_cluster_msg_topic";

	/**
	 * 
	 * @Description： 订阅主题
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年9月28日下午1:45:09]
	 * @param connectionFactory
	 * @param listenerAdapter
	 * @return
	 *
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// 订阅主题
		container.addMessageListener(listenerAdapter, new PatternTopic(topic));
		// 这个container 可以添加多个 messageListener
		return container;
	}

	/**
	 * 
	 * @Description： 消息接受的处理器
	 * 
	 * @author [ wenfengSAT@163.com ]
	 * @Date [2023年9月28日下午1:45:19]
	 * @param receiver
	 * @return
	 *
	 */
	@Bean
	public MessageListenerAdapter listenerAdapter(ClusterMessageReceive receiver) {
		// 这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
		// 也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage 可以自己到源码里面看
		return new MessageListenerAdapter(receiver, "receiveClusterMessage");
	}

}

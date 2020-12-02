### 启动配置环境变量参数

------
| 变量                      | 描述                                                         | 默认值              |
| ------------------------- | ------------------------------------------------------------ | :------------------ |
| PROFILES_ACTIVE           | 配置文件加载标记，根据标记启动时加载指定的配置文件           | redisCluster        |
| SERVER_PORT               | 服务启动端口                                                 | 9094                |
| LOG_DESAYSV_LEVEL         | com.apigate 包日志级别                                       | debug               |
| TOMCAT_THREADS            | 内置tomcat最大线程数                                         | 800                 |
| TOMCAT_ACCEPT_COUNT       | 内置tomcat被允许请求数                                       | 1000                |
| TOMCAT_MAX_CONNECTIONS    | 内置tomcat最大连接数                                         | 10000               |
| LOG_SPRINGFRAMEWORK_LEVEL | org.springframework 包日志级别                               | warn                |
| ZOOKEEPER_SERVER          | zookeeper集群地址                                            | 本地环境            |
| REDIS_CLUSTER_NODES       | redis集群模式地址，PROFILES_ACTIVE加载了redisCluster模式才生效 | 开发环境默认集群    |
| REDIS_HOST                | redis主从模式的IP地址，PROFILES_ACTIVE加载了redisHost模式才生效 | redis               |
| REDIS_PASSWORD            | redis主从模式的端口，PROFILES_ACTIVE加载了redisHost模式才生效 | 6379                |
| REDIS_PASSWORD            | redis密码，不区分模式                                        | 123456              |
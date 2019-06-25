package com.github.hdy.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * redis监听配置
 */
@Component
public class CustomListener implements ApplicationListener<ContextRefreshedEvent> {
    //是否自动初始化redis
    @Value("${redis.init:false}")
    private boolean init_redis;
    @Value("${redis.serviceName:null}")
    private String redis_serviceName;
    @Value("${redis.ip:null}")
    private String redis_ip;
    @Value("${redis.port:0}")
    private int redis_port;
    @Value("${redis.pwd:null}")
    private String redis_pwd;
    //连接到Redis实例允许的最大分配的对象数
    @Value("${redis.MaxTotal:0}")
    private int redis_maxTotal;
    //最大能够保持idel状态的对象数
    @Value("${redis.MaxIdle:0}")
    private int redis_maxIdle;
    //连接最大等待时间
    @Value("${redis.MaxWaitMillis:0}")
    //当调用borrow Object方法时，是否进行有效性检查
    private int redis_maxWaitMillis;
    @Value("${redis.TestOnBorrow:false}")
    private boolean redis_testOnBorrow;
    @Value("${regisType:redis}")
    private String regisType;
    @Value("${header.key:hedayeshinidie}")
    private String headerKey;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (init_redis) {
            /** 初始化redis */
            RedisBuilder.initRedis(redis_serviceName, redis_ip, redis_port,
                    redis_maxTotal, redis_maxIdle, redis_maxWaitMillis, redis_testOnBorrow, redis_pwd);
        }
    }
}

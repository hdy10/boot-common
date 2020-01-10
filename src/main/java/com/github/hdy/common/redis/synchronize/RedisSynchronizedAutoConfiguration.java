package com.github.hdy.common.redis.synchronize;

import com.github.hdy.common.redis.synchronize.aspect.RedisSynchronizedAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis锁配置
 */
@Configuration
@ConditionalOnWebApplication
public class RedisSynchronizedAutoConfiguration {

    @Bean
    public RedisSynchronizedAspect redisSynchronizedAspect() {
        return new RedisSynchronizedAspect();
    }
}

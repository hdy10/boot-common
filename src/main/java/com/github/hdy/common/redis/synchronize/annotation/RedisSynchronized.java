package com.github.hdy.common.redis.synchronize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis锁
 *
 * @author hdy
 * @date 2019/7/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisSynchronized {
    // 唯一key
    String key();

    // 几秒时间内不可重复提交
    long second() default 1L;

    String msg() default "请勿重复提交！";
}

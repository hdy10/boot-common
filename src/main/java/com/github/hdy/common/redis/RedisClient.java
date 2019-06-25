package com.github.hdy.common.redis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisClient {
    public static Map<String, Jedis> REDIS_MAP = new HashMap<String, Jedis>();

    public static Set<String> keys(SourceType serviceName, String pattern) {
        Jedis jedis = REDIS_MAP.get(serviceName.name);
        Set<String> set = jedis.keys(pattern);
        jedis.close();
        return set;
    }

}

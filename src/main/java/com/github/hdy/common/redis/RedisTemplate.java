package com.github.hdy.common.redis;

import com.github.hdy.common.util.Logs;
import com.github.hdy.common.util.Strings;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 * Redis
 *
 * @author hdy
 */

public class RedisTemplate {

    /**
     * 功能:设置key过期时间
     *
     * @param serviceName   Redis服务名
     * @param key           键
     * @param expireSeconds 过期时间(单位:秒)
     *
     * @return Boolean 是否设置过期时间异常
     *
     * @throws Exception
     */
    public static Boolean setKeyExpireTime(SourceType serviceName, String key, Integer expireSeconds) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        try {
            Long returnCode = jedisClient.expire(key, expireSeconds);
            return returnCode == 1L;
        } catch (Exception e) {
            throw e;
        } finally {
            RedisBuilder.releaseJedis(jedisClient);
        }
    }

    /**
     * 功能:设置String类型值
     *
     * @param serviceName Redis服务名
     * @param key         键
     * @param value       值
     *
     * @return
     */
    public static void setString(SourceType serviceName, String key, String value) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.set(key, value);
        RedisBuilder.releaseJedis(jedisClient);
    }


    /**
     * 功能:设置String类型值和过期时间
     *
     * @param serviceName Redis服务名
     * @param key         键
     * @param seconds     时间(秒)
     * @param value       值
     *
     * @return
     */
    public static void setExString(SourceType serviceName, String key, int seconds, String value) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.setex(key, seconds, value);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能:获取String类型值
     *
     * @param serviceName Redis服务名
     * @param key         键
     *
     * @return
     */
    public static String getString(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        String value = jedisClient.get(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    /**
     * 功能:新增有序集合
     * 有序集合的成员是唯一的,但分数(score)却可以重复。
     * 集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是O(1)。 集合中最大的成员数为 232 - 1 (4294967295, 每个集合可存储40多亿个成员)。
     *
     * @param serviceName Redis服务名
     * @param key         键
     *
     * @return
     */
    public static void zadd(SourceType serviceName, String key, double score, String value) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.zadd(key, score, value);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能：返回有序集中指定区间内的成员，通过索引，分数从高到底
     *
     * @param serviceName Redis服务名
     * @param key         键
     * @param start       开始
     * @param end         结束   查所有为-1  差第一条为0
     *
     * @return
     */
    public static Set<String> zrevrange(SourceType serviceName, String key, long start, long end) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Set<String> value = jedisClient.zrevrange(key, start, end);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    /**
     * 功能：返回有序集中指定区间内的成员与分值，通过索引，分数从高到底
     *
     * @param serviceName Redis服务名
     * @param key         键
     * @param start       开始
     * @param end         结束   查所有为-1  差第一条为0
     *
     * @return
     */
    public static Set<Tuple> zrevrangeWithScores(SourceType serviceName, String key, long start, long end) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Set<Tuple> value = jedisClient.zrevrangeWithScores(key, start, end);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }


    /**
     * 功能:为队列头添加值
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     * @param value       值
     */
    public static void pushListHead(SourceType serviceName, String key, String... value) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.rpush(key, value);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能:为队列尾添加值
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     * @param value       值
     */
    public static void pushListTail(SourceType serviceName, String key, String... value) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.lpush(key, value);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能:从队列头取出值
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static String popListHead(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        String value = jedisClient.lpop(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    /**
     * 功能:从队列尾取出值
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static String popListTail(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        String value = jedisClient.rpop(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    /**
     * 功能:获取队列长度 参数:
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static Long getListSize(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Long len = jedisClient.llen(key);
        RedisBuilder.releaseJedis(jedisClient);
        return len;
    }

    /**
     * 功能:判断SessionId是否存在缓存服务器中 参数:
     *
     * @param serviceName
     * @param key         sessionId
     *
     * @return Boolean true:存在 false:不存在
     *
     * @throws Exception
     */
    public static Boolean isExistKey(SourceType serviceName, String key) {
        if (null == key || "".equals(key)) {
            return false;
        }

        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        try {
            Boolean isExit = jedisClient.exists(key);
            return isExit;
        } catch (Exception e) {
            throw e;
        } finally {
            RedisBuilder.releaseJedis(jedisClient);
        }
    }

    /**
     * 功能:判断SessionId是否存在缓存服务器中,存在则删除
     *
     * @param serviceName
     * @param key         sessionId
     *
     * @return Boolean true:删除 false:不存在
     *
     * @throws Exception
     */
    public static Boolean removeKey(SourceType serviceName, String key) {
        if (null == key || "".equals(key)) {
            return false;
        }

        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        try {
            // Boolean isExit = jedisClient.exists(key);
            if (jedisClient.exists(key)) {
                long count = jedisClient.del(key);
                return count > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            RedisBuilder.releaseJedis(jedisClient);
        }
    }

    public static Long incrKey(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        try {
            Long value = jedisClient.incr(key);
            return value;
        } catch (Exception e) {
            throw e;
        } finally {
            RedisBuilder.releaseJedis(jedisClient);
        }
    }


    /**
     * 功能:将数据存到队列中:
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static void lpush(SourceType serviceName, byte[] key, byte[] strings) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.lpush(key, strings);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能:将数据存到队列中:
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static void lpush(SourceType serviceName, String key, String strings) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        jedisClient.lpush(key, strings);
        RedisBuilder.releaseJedis(jedisClient);
    }

    /**
     * 功能:获取队列中第一条:
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static byte[] lpop(SourceType serviceName, byte[] key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        byte[] value = jedisClient.lpop(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    /**
     * 功能:获取队列中第一条:
     *
     * @param serviceName Redis服务名
     * @param key         队列名
     *
     * @return
     */
    public static String lpop(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        String value = jedisClient.lpop(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }


    public static Long getLen(SourceType serviceName, byte[] key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Long value = jedisClient.llen(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }

    public static Long getLen(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Long value = jedisClient.llen(key);
        RedisBuilder.releaseJedis(jedisClient);
        return value;
    }


    /**
     * 获取所有的Lists
     *
     * @param serviceName
     * @param key
     *
     * @return
     */
    public static List<String> getList(SourceType serviceName, String key) {
        ShardedJedis jedisClient = RedisBuilder.getJedis(serviceName.name());
        Long len = jedisClient.llen(key);
        List<String> lists = jedisClient.lrange(key, 0, len);
        RedisBuilder.releaseJedis(jedisClient);
        return lists;
    }


    /**
     * 处理重复请求(针对单个用户时key需要加上用户唯一表示)
     *
     * @param key     建议使用当前接口的方法名
     * @param seconds 过期时间
     */
    public static boolean repeatRequest(String key, int seconds) {
        String s = RedisTemplate.getString(SourceType.Redis, key);
        if (!Strings.isNull(s)) {
            Logs.error("重复请求");
            return false;
        }
        RedisTemplate.setExString(SourceType.Redis, key, seconds, "判断是否重复请求");
        return true;
    }

}

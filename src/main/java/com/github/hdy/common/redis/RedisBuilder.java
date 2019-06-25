package com.github.hdy.common.redis;

import com.github.hdy.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.*;

import java.io.*;
import java.util.*;

public class RedisBuilder {
    private static Logger logger = LogManager.getLogger(RedisBuilder.class.getName());
    private static Map<String, ShardedJedisPool> REDIS_MAP = new HashMap<String, ShardedJedisPool>();

    public static void initRedis(String redis_serviceName, String redis_ip, int redis_port, int redis_maxTotal, int
            redis_maxIdle, int redis_maxWaitMillis, boolean redis_testOnBorrow, String redis_pwd) {
        // 池基本配置
        if (StringUtils.isNull(redis_serviceName, redis_ip, redis_port) ||
                (StringUtils.isEquals(redis_port, 0) ||
                        StringUtils.isEquals(redis_maxTotal, 0) ||
                        StringUtils.isEquals(redis_maxIdle, 0) ||
                        StringUtils.isEquals(redis_maxWaitMillis, 0))) {
            logger.error("redis开启失败");
            default_redis();
            return;
        }

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redis_maxTotal);
        config.setMaxIdle(redis_maxIdle);
        config.setMaxWaitMillis(redis_maxWaitMillis);
        config.setTestOnBorrow(redis_testOnBorrow);
        String[] str = null;
        if (redis_serviceName.contains(",")) {
            str = redis_serviceName.split(",");
        } else {
            str = new String[]{redis_serviceName};
        }
        for (int i = 0; i < str.length; i++) {
            logger.info("serviceName:{},ip:{},port:{},MaxTotal:{},MaxIdle:{},MaxWaitMillis:{},TestOnBorrow:{}", str[i], redis_ip, redis_port, redis_maxTotal, redis_maxIdle, redis_maxWaitMillis, redis_testOnBorrow);
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
            JedisShardInfo jedisShardInfo = new JedisShardInfo(redis_ip, redis_port);
            if (!StringUtils.isNull(redis_pwd)) {
                jedisShardInfo.setPassword(redis_pwd);
            }
            shards.add(jedisShardInfo);
            ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);
            REDIS_MAP.put(str[i], shardedJedisPool);

            Jedis jedis = new Jedis(redis_ip, redis_port);
            if (!StringUtils.isNull(redis_pwd))
                jedis.auth(redis_pwd);
            RedisClient.REDIS_MAP.put(str[i], jedis);
        }

        logger.info("\u001b[48;32mredis开启成功\u001b[0m");
        /*Boolean initFlag = true;
        Properties properties = new Properties();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = RedisBuilder.class.getClassLoader().getResourceAsStream("/default_redis/redis.properties");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            properties.load(bufferedReader);
            initialPool(properties);
        } catch (FileNotFoundException e) {
            logger.error("无法找到文件:");
            return null;
        } catch (IOException e) {
            logger.error("读文件出错:");
            return null;
        }finally {
            try {
                if(null != inputStream)
                    inputStream.close();
                if(null != bufferedReader)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            properties.clone();
        }
        return initFlag;*/
    }

    private static void default_redis() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = RedisBuilder.class.getResourceAsStream("/default_redis/redis.properties");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            properties.load(bufferedReader);
            initialPool(properties);
        } catch (FileNotFoundException e) {
            logger.error("无法找到文件:");
        } catch (IOException e) {
            logger.error("读文件出错:");
        } finally {
            try {
                if (null != inputStream)
                    inputStream.close();
                if (null != bufferedReader)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            properties.clone();
        }
    }

    /**
     * 功能:初始化Redis连接客户端
     *
     * @param confProp Redis服务器配置信息
     */
    private static void initialPool(Properties confProp) {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        String ip = confProp.getProperty("ip");
        Integer port = Integer.valueOf(confProp.getProperty("port"));
        String pwd = confProp.getProperty("pwd");
        Integer MaxTotal = Integer.valueOf(confProp.getProperty("MaxTotal"));
        Integer MaxIdle = Integer.valueOf(confProp.getProperty("MaxIdle"));
        Long MaxWaitMillis = Long.valueOf(confProp.getProperty("MaxWaitMillis"));
        Boolean TestOnBorrow = Boolean.valueOf(confProp.getProperty("TestOnBorrow"));

        config.setMaxTotal(MaxTotal);
        config.setMaxIdle(MaxIdle);
        config.setMaxWaitMillis(MaxWaitMillis);
        config.setTestOnBorrow(TestOnBorrow);

        String serviceName = confProp.getProperty("serviceName");
        String[] str = null;
        if (serviceName.contains(",")) {
            str = serviceName.split(",");
        } else {
            str = new String[]{serviceName};
        }
        for (int i = 0; i < str.length; i++) {
            logger.info("serviceName:{},ip:{},port:{},MaxTotal:{},MaxIdle:{},MaxWaitMillis:{},TestOnBorrow:{}", str[i], ip, port, MaxTotal, MaxIdle, MaxWaitMillis, TestOnBorrow);
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
            JedisShardInfo jedisShardInfo = new JedisShardInfo(ip, port, 100000);
            if (!StringUtils.isNull(pwd)) {
                jedisShardInfo.setPassword(pwd);
            }
            shards.add(jedisShardInfo);
            ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);
            REDIS_MAP.put(str[i], shardedJedisPool);

            Jedis jedis = new Jedis(ip, port);
            if (null != pwd && pwd.length() > 0)
                jedis.auth(pwd);
            RedisClient.REDIS_MAP.put(str[i], jedis);
        }

    }

    /**
     * 功能:读取配置文件
     *
     * @param filePath 配置文件路径
     *
     * @return Properties 配置项信息
     */
    public Properties loadProperties(String filePath) {
        Properties confProp = new Properties();
        InputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            confProp.load(in);
        } catch (FileNotFoundException exp) {
            logger.error(exp.getMessage());
        } catch (IOException exp) {
            logger.error(exp.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exp) {
                    logger.error(exp.getMessage());
                }
            }
        }

        return confProp;
    }

    public static ShardedJedis getJedis(String serviceName) {
        ShardedJedisPool jedisPool = REDIS_MAP.get(serviceName);
        return jedisPool.getResource();
    }

    public static void releaseJedis(ShardedJedis jedis) {
        jedis.close();
    }

}

package com.rishiqing.util.http.ratelimiter.adapter;

import com.rishiqing.util.http.ratelimiter.*;
import redis.clients.jedis.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 为防止系统发生bug导致的锁一直存在，因此设置的两次请求的最长时间间隔不能超过60000ms即60秒
 * @author Wallace Mao
 * Date: 2019-02-21 15:55
 */
public class ShareStoreAdapterRedisImpl implements ShareStoreAdapter {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "6379";
    private static final String DEFAULT_PASSWORD = null;
    private static final int DEFAULT_CONNECT_TIMEOUT = 2000;

    //  key存在的最大时长
    static final int KEY_MAX_SURVIVE_MILLS = 120000;
    private static final int DEFAULT_SLEEP_MILLS = 100;

    private Map<String, String> redisConfig;
    private JedisPool pool;

    public ShareStoreAdapterRedisImpl() {
        this.redisConfig = new HashMap<>();
    }

    public ShareStoreAdapterRedisImpl(Map<String, String> configMap) {
        this.redisConfig = configMap;
    }

    @Override
    public void connect() {
        String host = redisConfig.getOrDefault("host", DEFAULT_HOST);
        int port = Integer.valueOf(redisConfig.getOrDefault("port", DEFAULT_PORT));
        String password = redisConfig.getOrDefault("password", DEFAULT_PASSWORD);

        System.out.println("host: " + host + ", port: " + port + ", password: " + password);
        this.pool = new JedisPool(new JedisPoolConfig(), host, port, DEFAULT_CONNECT_TIMEOUT, password);
    }

    @Override
    public LimitLock requestLock(String key, Bandwidth bandwidth, Long timeoutMills) throws RequestLockException, TimeoutException {
        try {
            long expireMills = bandwidth.calcIntervalMills();
            if (this.pool == null) {
                connect();
            }
            long currentRedisMills = getTimeMills();
            String info = String.valueOf(currentRedisMills);
            //  清理过时的锁
            clearDeprecatedKey(key, currentRedisMills);

            long nowMills = new Date().getTime();
            while (true) {
                // 超时就清理数据并返回错误
                if (new Date().getTime() - nowMills > timeoutMills) {
                    deleteKey(key);
                    throw RequestLimiterExceptions.waitTimeout(key, new Date().getTime() - nowMills, timeoutMills);
                }
                //  尝试获取锁，如果成功就返回锁，否则就过段时间重试
                if (setIfNotExistsAndExpire(key, info, expireMills)) {
                    return new LimitLock(key, info);
                } else {
                    Thread.sleep(DEFAULT_SLEEP_MILLS);
                }
            }
        } catch(TimeoutException e) {
            throw e;
        }catch (Exception e) {
            throw RequestLimiterExceptions.anyExceptionWhenRequestLock(e);
        }
    }

    @Override
    public void releaseLock(String key) {
        deleteKey(key);
    }

    @Override
    public void shutdown() {
        this.pool.close();
    }

    long getTimeMills() {
        try (Jedis jedis = pool.getResource()) {
            return Long.valueOf(jedis.time().get(0)) * 1000;
        }
    }

    String getKey(String key) {
        try (Jedis jedis = pool.getResource()){
            return jedis.get(key);
        }
    }

    void deleteKey(String key) {
        try (Jedis jedis = pool.getResource()){
            jedis.del(key);
        }
    }

    /**
     * 检查是否有过时的锁，即检查key的value值是否超过了KEY_MAX_SURVIVE_MILLS，如果超过，那么会做删除处理
     * @param key
     * @param currentRedisTime
     */
    void clearDeprecatedKey(String key, long currentRedisTime) {
        try (Jedis jedis = pool.getResource()){
            String value = jedis.get(key);
            if (value != null && currentRedisTime - Long.valueOf(value) > KEY_MAX_SURVIVE_MILLS) {
                jedis.del(key);
            }
        }
    }

    boolean setIfNotExistsAndExpire(String key, String value, Long expireMills) {
        long result;
        try (Jedis jedis = pool.getResource()){
            result = jedis.setnx(key, value);
            //  result表示成功set的数量，当为0时表示已经存在，没有set成功
            if (result > 0) {
                jedis.pexpire(key, expireMills);
                return true;
            } else {
                return false;
            }
        }
    }
}

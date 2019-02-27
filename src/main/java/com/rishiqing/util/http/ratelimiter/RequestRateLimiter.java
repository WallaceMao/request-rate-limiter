package com.rishiqing.util.http.ratelimiter;

import java.util.concurrent.TimeoutException;

/**
 * 请求频率限制器，在发送api请求的时候，避免由于请求频率过高而导致被钉钉或者企业微信的服务器拒绝
 * 默认使用redis作为频率共享存储的介质
 * @author Wallace Mao
 * Date: 2019-02-21 15:52
 */
public class RequestRateLimiter {
    private Configuration configuration;
    private ShareStoreAdapter adapter;

    RequestRateLimiter(Configuration configuration, ShareStoreAdapter adapter){
        this.configuration = configuration;
        this.adapter = adapter;
    }

    public void start() {
        this.adapter.connect();
    }

    public void queryLock() throws RequestLockException, TimeoutException {
        queryLock(this.configuration.getDefaultKey(), this.configuration.getDefaultTimeoutMills());
    }

    public void queryLock(String key) throws RequestLockException, TimeoutException {
        queryLock(key, this.configuration.getDefaultTimeoutMills());
    }

    public void queryLock(String key, Long timeoutMills) throws RequestLockException, TimeoutException {
        this.adapter.requestLock(key, this.configuration.getBandwidth(), timeoutMills);
    }

    public void releaseLock() {
        releaseLock(this.configuration.getDefaultKey());
    }

    public void releaseLock(String key) {
        this.adapter.releaseLock(key);
    }

    public void shutdown() {
        this.adapter.shutdown();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ShareStoreAdapter getAdapter() {
        return adapter;
    }
}

package com.rishiqing.util.http.ratelimiter;

import java.util.concurrent.TimeoutException;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 15:55
 */
public interface ShareStoreAdapter {
    void connect();

    LimitLock requestLock(String key, Bandwidth bandwidth, Long timeoutMills) throws RequestLockException, TimeoutException;

    void releaseLock(String key);

    void shutdown();
}

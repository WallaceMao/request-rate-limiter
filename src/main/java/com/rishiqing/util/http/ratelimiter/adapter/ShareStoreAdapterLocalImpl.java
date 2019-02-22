package com.rishiqing.util.http.ratelimiter.adapter;

import com.rishiqing.util.http.ratelimiter.Bandwidth;
import com.rishiqing.util.http.ratelimiter.LimitLock;
import com.rishiqing.util.http.ratelimiter.RequestLockException;
import com.rishiqing.util.http.ratelimiter.ShareStoreAdapter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 17:18
 */
public class ShareStoreAdapterLocalImpl implements ShareStoreAdapter {
    @Override
    public void connect() {

    }

    @Override
    public LimitLock requestLock(String key, Bandwidth bandwidth, Long timeoutMills) throws RequestLockException {
        return null;
    }

    @Override
    public void releaseLock(String key) {

    }

    @Override
    public void shutdown() {

    }
}

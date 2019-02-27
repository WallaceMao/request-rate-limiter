package com.rishiqing.util.http.ratelimiter;

import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterRedisImpl;

import java.time.Duration;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:06
 */
public class Bandwidth {
    private final long requestCount;
    private final Duration duration;

    private Bandwidth(long requestNum, Duration duration) {
        this.requestCount = requestNum;
        this.duration = duration;
    }

    public static Bandwidth simple (Long requestCount, Duration duration) {
        if (requestCount == null || requestCount < 0) {
            throw RequestLimiterExceptions.invalidRequestCount(requestCount);
        }
        if (duration == null) {
            throw RequestLimiterExceptions.invalidRequestDuration(duration);
        }
        return new Bandwidth(requestCount, duration);
    }

    public Long calcIntervalMills() {
        return this.duration.getSeconds() * 1000 / this.requestCount;
    }
}

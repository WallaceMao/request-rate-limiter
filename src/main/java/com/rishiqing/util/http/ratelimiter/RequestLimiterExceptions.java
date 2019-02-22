package com.rishiqing.util.http.ratelimiter;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:11
 */
public class RequestLimiterExceptions {
    public static IllegalArgumentException invalidRequestCount(Long requestCount) {
        String pattern = "{0} is invalid for request count";
        String msg = MessageFormat.format(pattern, requestCount);
        return new IllegalArgumentException(msg);
    }

    public static IllegalArgumentException invalidRequestDuration(Duration duration) {
        String pattern = "{0} is invalid for request duration";
        String msg = MessageFormat.format(pattern, duration);
        return new IllegalArgumentException(msg);
    }

    public static IllegalArgumentException invalidBandwith(Bandwidth bandwidth) {
        String pattern = "{0} is invalid for bandwidth";
        String msg = MessageFormat.format(pattern, bandwidth);
        return new IllegalArgumentException(msg);
    }

    public static TimeoutException waitTimeout(String key, Long waitMills, Long timeoutMills) {
        String pattern = "request lock timeout, key: {0}, wait for {1}ms, exceeding timeout {2}ms, you may change the default timeout";
        String msg = MessageFormat.format(pattern, key, waitMills,timeoutMills);
        return new TimeoutException(msg);
    }

    public static RequestLockException anyExceptionWhenRequestLock(Exception e) {
        return new RequestLockException(e);
    }
}

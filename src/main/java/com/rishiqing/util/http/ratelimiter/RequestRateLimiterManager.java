package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:27
 */
public class RequestRateLimiterManager {
    private RequestRateLimiterManager(){}

    public static RequestRateLimiterBuilder builder() {
        return new RequestRateLimiterBuilder();
    }
}

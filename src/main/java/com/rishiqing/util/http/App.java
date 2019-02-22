package com.rishiqing.util.http;

import com.rishiqing.util.http.ratelimiter.*;
import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterLocalImpl;
import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterRedisImpl;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:50
 */
public class App {
    public static void main(String[] args){
        Bandwidth bandwidth = Bandwidth.simple(10L, Duration.ofMinutes(1));
        Map<String, String> config = new HashMap<>();
        ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl(config);
        RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                .addLimit(bandwidth)
                .setAdapter(adapter)
                .build();
        new App().checkRequestRate(limiter);
    }

    private void checkRequestRate(RequestRateLimiter limiter){

        Date now = new Date();
        System.out.println("----begin: " + now);
        try {
            while (true) {
                if (new Date().getTime() - now.getTime() > 3000L) {
                    break;
                }
                limiter.queryLock("hello", 60000L);
                printTime(now.getTime());
            }
        } catch (RequestLockException e) {
            e.printStackTrace();
        }
        printTime(now.getTime());
    }

    private void printTime(Long base) {
        Date now = new Date();
        System.out.println("----Date: " + now + ", elapse: " + (now.getTime() - base) + "ms");
    }
}

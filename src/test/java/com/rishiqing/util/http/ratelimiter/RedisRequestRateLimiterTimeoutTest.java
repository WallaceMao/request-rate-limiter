package com.rishiqing.util.http.ratelimiter;

import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterRedisImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Wallace Mao
 * Date: 2019-02-22 19:01
 */
public class RedisRequestRateLimiterTimeoutTest {
    private Long MAX_INTERVAL_ERROR_MILLS = 200L;
    private Bandwidth defaultBandwith;
    private Map<String, String> redisConfig;
    private List<String> keyList;
    private RequestRateLimiter limiter;

    @BeforeClass
    public void beforeClass() {
        defaultBandwith = Bandwidth.simple(30L, Duration.ofMinutes(1));
        redisConfig = new HashMap<>();
        redisConfig.put("host", "127.0.0.1");
        redisConfig.put("port", "6379");
        ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl(redisConfig);
        limiter = RequestRateLimiterManager.builder()
                .addLimit(defaultBandwith)
                .setAdapter(adapter)
                .build();
    }

    @Test
    public void testTimeout() throws Exception {
        assertThatThrownBy(() -> {
            limiter.queryLock("mykey", defaultBandwith.calcIntervalMills() / 2);
            limiter.queryLock("mykey", defaultBandwith.calcIntervalMills() / 2);
        }).isInstanceOf(TimeoutException.class);
    }
}

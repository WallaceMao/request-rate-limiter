package com.rishiqing.util.http.ratelimiter.adapter;

import com.rishiqing.util.http.ratelimiter.Bandwidth;
import com.rishiqing.util.http.ratelimiter.RequestRateLimiter;
import com.rishiqing.util.http.ratelimiter.RequestRateLimiterManager;
import com.rishiqing.util.http.ratelimiter.ShareStoreAdapter;
import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterLocalImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author Wallace Mao
 * Date: 2019-02-26 16:38
 */
public class ShareStoreAdapterLocalTest {
    private ShareStoreAdapter localAdapter;

    @BeforeClass
    public void beforeClass() {
        localAdapter = new ShareStoreAdapterLocalImpl();
    }
    @Test
    public void testNotImplemented() {
        assertThatCode(() -> {
            localAdapter.connect();
            localAdapter.requestLock("KEY", Bandwidth.simple(1L, Duration.ofSeconds(1)), 2000L);
            localAdapter.releaseLock("KEY");
            localAdapter.shutdown();
        }).doesNotThrowAnyException();
    }

    @Test
    public void testDefaultAdapter() throws Exception {
        RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                .addLimit(Bandwidth.simple(10L, Duration.ofSeconds(1)))
                .build();
        assertThat(limiter.getAdapter()).isInstanceOf(ShareStoreAdapterLocalImpl.class);
    }
}

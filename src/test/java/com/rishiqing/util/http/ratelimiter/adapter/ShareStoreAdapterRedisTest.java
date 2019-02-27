package com.rishiqing.util.http.ratelimiter.adapter;

import com.rishiqing.util.http.ratelimiter.*;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * @author Wallace Mao
 * Date: 2019-02-26 16:38
 */
public class ShareStoreAdapterRedisTest {

    @Test
    public void testDefaultConstructor() throws Exception {
        ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl();
        RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                .addLimit(Bandwidth.simple(30L, Duration.ofMinutes(1)))
                .setAdapter(adapter)
                .build();

        limiter.queryLock();
    }

    @Test
    public void testInterrupt() throws Exception {
        final Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mainThread.interrupt();
        }).start();

        assertThatThrownBy(() -> {
            ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl();
            final RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                    .addLimit(Bandwidth.simple(60L, Duration.ofMinutes(1)))
                    .setAdapter(adapter)
                    .build();

            limiter.queryLock();
            limiter.queryLock();
        }).isInstanceOf(RequestLockException.class)
                .hasCauseInstanceOf(InterruptedException.class);
    }

    @Test
    public void testDeprecatedKey() throws Exception {
        String key = "myspykey";
        long nowMills = new Date().getTime();
        long deprecatedMills = nowMills + ShareStoreAdapterRedisImpl.KEY_MAX_SURVIVE_MILLS + 1000;
        Bandwidth bandwidth = Bandwidth.simple(60L, Duration.ofMinutes(1));

        //  使用spy来修改getTimeMills的值
        ShareStoreAdapterRedisImpl spy = spy(new ShareStoreAdapterRedisImpl());
        doReturn(nowMills).doReturn(deprecatedMills).when(spy).getTimeMills();

        //  首次使用锁，使用nowMills作为redis的时间
        final RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                .addLimit(bandwidth)
                .setAdapter(spy)
                .build();

        limiter.queryLock(key);
        assertThat(spy.getKey(key)).isEqualTo(String.valueOf(nowMills));

        //  第二次使用锁，使用deprecatedMills来模拟redis的时间。由于deprecatedMills已经超时，因此不会做等待
        long beforeQueryLockMills = new Date().getTime();
        limiter.queryLock(key);
        long afterQueryLockMills = new Date().getTime();
        assertThat(afterQueryLockMills - beforeQueryLockMills).isLessThan(bandwidth.calcIntervalMills());

        verify(spy, times(2)).getTimeMills();
    }
}

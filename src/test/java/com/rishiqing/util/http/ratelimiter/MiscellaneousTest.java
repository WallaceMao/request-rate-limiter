package com.rishiqing.util.http.ratelimiter;

import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Wallace Mao
 * Date: 2019-02-26 16:18
 */
public class MiscellaneousTest {
    @Test
    public void testConfigurationDefaultValue() {
        Bandwidth bandwidth = Bandwidth.simple(10L, Duration.ofSeconds(1));
        RequestRateLimiter limiter = RequestRateLimiterManager.builder()
                .addLimit(bandwidth)
                .build();
        Configuration config = limiter.getConfiguration();

        assertThat(config.getDefaultKey()).isEqualTo("limit-default");
        assertThat(config.getDefaultTimeoutMills()).isEqualTo(2000L);
        assertThat(config.getBandwidth()).isEqualTo(bandwidth);
    }

    @Test
    public void testRequestLockException() {
        assertThatThrownBy(() -> {
            throw RequestLimiterExceptions.anyExceptionWhenRequestLock(new InterruptedException());
        }).hasCauseInstanceOf(InterruptedException.class);

        assertThatThrownBy(() -> {
            Bandwidth.simple(null, Duration.ofSeconds(1));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid for request count");
        assertThatThrownBy(() -> {
            Bandwidth.simple(-1L, Duration.ofSeconds(1));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid for request count");
        assertThatThrownBy(() -> {
            Bandwidth.simple(1L, null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid for request duration");

        assertThatThrownBy(() -> {
            RequestRateLimiterManager.builder()
                    .addLimit(null)
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid for bandwidth");
    }

    @Test
    public void testLimitLock() {
        Long now = new Date().getTime();
        String lockKey = "KEY-" + now;
        String lockValue = "VAL-" + now;

        LimitLock lock = new LimitLock(lockKey, lockValue);

        assertThat(lock.getLockKey()).isEqualTo(lockKey);
        assertThat(lock.getLockValue()).isEqualTo(lockValue);
        assertThat(lock.toString()).contains("LimitLock");
    }
}

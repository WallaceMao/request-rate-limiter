package com.rishiqing.util.http.ratelimiter;

import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterRedisImpl;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wallace Mao
 * Date: 2019-02-22 19:01
 */
public class RedisRequestRateLimiterSingleKeyTest {
    private Long MAX_INTERVAL_ERROR_MILLS = 200L;
    private Bandwidth defaultBandwith;
    private Map<String, String> redisConfig;
    private List<String> keyList;
    private RequestRateLimiter limiter;

    private List<ThreadInfo> singleKeyConcurrencyResult;
    private List<ThreadInfo> singleKeyConcurrencyResultDefaultValue;

    @BeforeClass
    public void beforeClass() {
        defaultBandwith = Bandwidth.simple(40L, Duration.ofMinutes(1));
        redisConfig = new HashMap<>();
        redisConfig.put("host", "127.0.0.1");
        redisConfig.put("port", "6379");
        ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl(redisConfig);
        limiter = RequestRateLimiterManager.builder()
                .addLimit(defaultBandwith)
                .setAdapter(adapter)
                .build();
        limiter.start();
        singleKeyConcurrencyResult = Collections.synchronizedList(new ArrayList<>());
        singleKeyConcurrencyResultDefaultValue = Collections.synchronizedList(new ArrayList<>());
    }

    @AfterClass
    public void afterClass() throws Exception {
        testSingleKeyConcurrencyResult(
                singleKeyConcurrencyResult,
                "testSingleKeyConcurrency");
        testSingleKeyConcurrencyResult(
                singleKeyConcurrencyResultDefaultValue,
                "testSingleKeyConcurrencyDefaultValue");
        limiter.releaseLock("mykey");
        limiter.releaseLock();
        limiter.shutdown();
    }

    @Test(threadPoolSize = 3, invocationCount = 4, timeOut = 10000)
    public void testSingleKeyConcurrency() throws Exception {
        ThreadInfo threadInfo = new ThreadInfo();
        threadInfo.setThreadId(Thread.currentThread().getId());
        Date begin = new Date();
        threadInfo.setDateStart(begin);

        limiter.queryLock("mykey", 60000L);

        Date end = new Date();
        threadInfo.setDateEnd(end);
        singleKeyConcurrencyResult.add(threadInfo);
    }

    @Test(threadPoolSize = 2, invocationCount = 2, timeOut = 10000)
    public void testSingleKeyConcurrencyDefaultValue() throws Exception {
        ThreadInfo threadInfo = new ThreadInfo();
        threadInfo.setThreadId(Thread.currentThread().getId());
        Date begin = new Date();
        threadInfo.setDateStart(begin);

        limiter.queryLock();

        Date end = new Date();
        threadInfo.setDateEnd(end);
        singleKeyConcurrencyResultDefaultValue.add(threadInfo);
    }

    private void testSingleKeyConcurrencyResult(List<ThreadInfo> list, String testMethodName) throws NoSuchMethodException {
        int count = this.getClass().getMethod(testMethodName).getAnnotation(Test.class).invocationCount();
        list.sort((o1, o2) -> (int)(o1.getDateEnd().getTime() - o2.getDateEnd().getTime()));
        assertThat(list).hasSize(count);
        ThreadInfo first = list.get(0);
        Date startDate = first.getDateStart();
        assertThat(startDate).isCloseTo(first.getDateEnd(), MAX_INTERVAL_ERROR_MILLS);
        for (int i = 0; i < list.size(); i++) {
            ThreadInfo current = list.get(i);
            if (i < list.size() - 1) {
                assertThat(list.get(i + 1).getDateEnd()).isCloseTo(
                        new Date(current.getDateEnd().getTime() + defaultBandwith.calcIntervalMills()),
                        MAX_INTERVAL_ERROR_MILLS);
            }
        }
    }
}

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
public class RedisRequestRateLimiterMultiKeyTest {
    private Long MAX_INTERVAL_ERROR_MILLS = 200L;
    private Bandwidth defaultBandwith;
    private Map<String, String> redisConfig;
    private List<String> keyList;
    private RequestRateLimiter limiter;

    private List<ThreadInfo> multiKeyConcurrencyResult;
    private List<ThreadInfo> multiKeyConcurrencyResultDefaultTimeout;

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
        multiKeyConcurrencyResult = Collections.synchronizedList(new ArrayList<>());
        multiKeyConcurrencyResultDefaultTimeout = Collections.synchronizedList(new ArrayList<>());
    }

    @AfterClass
    public void afterClass() throws Exception {
        testMultiKeyConcurrencyResult(
                multiKeyConcurrencyResult,
                "testMultiKeyConcurrency");
        testMultiKeyConcurrencyResult(
                multiKeyConcurrencyResultDefaultTimeout,
                "testMultiKeyConcurrencyDefaultTimeout");
    }


    @Test(threadPoolSize = 3, invocationCount = 3, timeOut = 10000)
    public void testMultiKeyConcurrency() throws Exception {
        ThreadInfo threadInfo = new ThreadInfo();
        threadInfo.setThreadId(Thread.currentThread().getId());
        Date begin = new Date();
        threadInfo.setDateStart(begin);

        limiter.queryLock(String.valueOf(threadInfo.getThreadId()), 60000L);

        Date end = new Date();
        threadInfo.setDateEnd(end);
        multiKeyConcurrencyResult.add(threadInfo);
    }

    @Test(threadPoolSize = 3, invocationCount = 3, timeOut = 10000)
    public void testMultiKeyConcurrencyDefaultTimeout() throws Exception {
        ThreadInfo threadInfo = new ThreadInfo();
        threadInfo.setThreadId(Thread.currentThread().getId());
        Date begin = new Date();
        threadInfo.setDateStart(begin);

        limiter.queryLock(String.valueOf(threadInfo.getThreadId()));

        Date end = new Date();
        threadInfo.setDateEnd(end);
        multiKeyConcurrencyResultDefaultTimeout.add(threadInfo);
    }

    private void testMultiKeyConcurrencyResult(List<ThreadInfo> list, String testMethodName) throws NoSuchMethodException {
        int count = this.getClass().getMethod(testMethodName).getAnnotation(Test.class).invocationCount();
        list.sort((o1, o2) -> (int)(o1.getDateEnd().getTime() - o2.getDateEnd().getTime()));
        assertThat(list).hasSize(count);
        for (int i = 0; i < list.size(); i++) {
            ThreadInfo current = list.get(i);
            assertThat(current.getDateStart()).isCloseTo(current.getDateEnd(), MAX_INTERVAL_ERROR_MILLS);
        }
    }
}

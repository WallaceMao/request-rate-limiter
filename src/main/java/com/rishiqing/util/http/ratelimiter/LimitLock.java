package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 17:46
 */
public class LimitLock {
    private String lockKey;
    private String lockValue;

    public LimitLock(String lockKey, String lockValue) {
        this.lockKey = lockKey;
        this.lockValue = lockValue;
    }

    public String getLockKey() {
        return lockKey;
    }

    public String getLockValue() {
        return lockValue;
    }

    @Override
    public String toString() {
        return "LimitLock{" +
                "lockKey='" + lockKey + '\'' +
                ", lockValue='" + lockValue + '\'' +
                '}';
    }
}

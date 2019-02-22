package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 15:56
 */
class Configuration {
    private static final Long DEFAULT_TIMEOUT_MILLS = 2000L;  //  默认2s的超时时间
    private static final String DEFAULT_KEY = "limit-default";

    private Bandwidth bandwidth;

    Configuration(Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getDefaultKey() {
        return DEFAULT_KEY;
    }

    public Long getDefaultTimeoutMills() {
        return DEFAULT_TIMEOUT_MILLS;
    }

    public Bandwidth getBandwidth() {
        return this.bandwidth;
    }
}

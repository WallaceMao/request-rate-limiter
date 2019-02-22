package com.rishiqing.util.http.ratelimiter;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:01
 */
class ConfigurationBuilder {
    private Bandwidth bandwidth;

    protected ConfigurationBuilder() {}

    ConfigurationBuilder addLimit(Bandwidth bandwidth) {
        if (bandwidth == null) {
            throw RequestLimiterExceptions.invalidBandwith(bandwidth);
        }
        this.bandwidth = bandwidth;
        return this;
    }

    Configuration build() {
        //  计算interval
        return new Configuration(this.bandwidth);
    }
}

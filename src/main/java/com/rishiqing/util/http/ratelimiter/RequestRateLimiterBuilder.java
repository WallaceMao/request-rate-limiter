package com.rishiqing.util.http.ratelimiter;

import com.rishiqing.util.http.ratelimiter.adapter.ShareStoreAdapterLocalImpl;

/**
 * @author Wallace Mao
 * Date: 2019-02-21 16:27
 */
public class RequestRateLimiterBuilder {
    private final ConfigurationBuilder configurationBuilder;
    private ShareStoreAdapter shareStoreAdapter;

    protected RequestRateLimiterBuilder(){
        this.configurationBuilder = new ConfigurationBuilder();
    }

    public RequestRateLimiterBuilder addLimit(Bandwidth bandwidth) {
        this.configurationBuilder.addLimit(bandwidth);
        return this;
    }

    public RequestRateLimiterBuilder setAdapter(ShareStoreAdapter shareStoreAdapter) {
        this.shareStoreAdapter = shareStoreAdapter;
        return this;
    }

    public RequestRateLimiter build() {
        Configuration configuration = this.configurationBuilder.build();
        if (this.shareStoreAdapter == null) {
            this.shareStoreAdapter = new ShareStoreAdapterLocalImpl();
        }
        return new RequestRateLimiter(configuration, this.shareStoreAdapter);
    }
}

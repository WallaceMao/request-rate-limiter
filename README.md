# 请求的频率控制器

## 实现方案

使用全局锁来实现，全局锁的存储默认使用redis，也可以自行写adapter适配器来实现

## basic usage

基本实现是固定时间间隔来实现的，即如果设置一分钟允许10次调用，那么等同于每6秒中调用一次

limiter构建：
```
//  1分钟允许调用10次
Bandwidth bandwidth = Bandwidth.simple(10L, Duration.ofMinutes(1));
Map<String, String> config = new HashMap<>();
//  config.put("host", "127.0.0.1");
//  config.put("port", "6379");
//  config.put("password", "");
ShareStoreAdapter adapter = new ShareStoreAdapterRedisImpl(config);
RequestRateLimiter limiter = RequestRateLimiterManager.builder()
        .addLimit(bandwidth)
        .setAdapter(adapter)
        .build();
```

limiter使用：调用queryLock会堵塞当前线程，直到获取到锁或者超时
```
//  请求默认的key值
limiter.queryLock();
//  请求key值对应的lock
limiter.queryLock("hello");
//  请求key值对应的lock，并设置超时时间
limiter.queryLock("hello", 60000L);
```
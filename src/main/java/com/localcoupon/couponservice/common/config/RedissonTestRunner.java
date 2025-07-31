package com.localcoupon.couponservice.common.config;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonTestRunner implements CommandLineRunner {

    private final RedissonClient redissonClient;

    @Override
    public void run(String... args) {
        RBucket<String> bucket = redissonClient.getBucket("sentinel-test");
        bucket.set("connected");

        String value = bucket.get();
        System.out.println("✅ Redisson Sentinel 연결 성공, 값 = " + value);
    }
}


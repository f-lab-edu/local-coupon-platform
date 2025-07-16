package com.localcoupon.couponservice.common.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "localcoupon.redis")
public record RedisProperties(
        Duration sessionTtl,
        String sessionPrefix,
        String couponReadyPrefix,
        String couponOpenPrefix,
        String couponLockPrefix,
        String duplicateRequestLockPrefix
) {
}

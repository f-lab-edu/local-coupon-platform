package com.localcoupon.couponservice.common.dto;

import java.util.concurrent.atomic.AtomicInteger;

public record RateLimitInfo(AtomicInteger counter, long lastRequestTime) {
    public static RateLimitInfo init(long currentTimeMillis) {
        return new RateLimitInfo(new AtomicInteger(1), currentTimeMillis);
    }
    public RateLimitInfo calculate() {
        int newCount = this.counter.incrementAndGet();
        return new RateLimitInfo(new AtomicInteger(newCount), this.lastRequestTime);
    }

}

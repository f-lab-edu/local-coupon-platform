package com.localcoupon.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@AllArgsConstructor(staticName = "of")
public class RateLimitInfo {
    private final AtomicInteger counter;
    public final long lastRequestTime;

    public static RateLimitInfo init(long now) {
        return new RateLimitInfo(new AtomicInteger(1), now);
    }

    public RateLimitInfo calculate() {
        counter.incrementAndGet();
        return this;
    }
}

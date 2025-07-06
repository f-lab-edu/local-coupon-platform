package com.localcoupon.couponservice.common.infra;

import java.time.Duration;


public final class RedisConstants {
    public static final Duration SESSION_TTL = Duration.ofHours(1);
    public static final String SESSION_PREFIX = "SESSION:";
    public static final String COUPON_READY_PREFIX = "coupon:ready:";
    public static final String COUPON_OPEN_PREFIX = "coupon:open:";
    public static final String COUPON_LOCK_PREFIX = "coupon:lock:";
}


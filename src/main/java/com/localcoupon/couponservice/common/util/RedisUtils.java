package com.localcoupon.couponservice.common.util;

import java.time.Duration;

public final class RedisUtils {
    public static final Duration SESSION_TTL = Duration.ofHours(1);
    public static final String SESSION_PREFIX = "SESSION:";
    public static final String COUPON_LOCK_PREFIX = "LOCK:COUPON:";
}


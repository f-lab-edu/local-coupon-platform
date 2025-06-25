package com.localcoupon.couponservice.global.util;

public class ClientUtils {
    public static String extractClientIp(String x_forwarded_for, String remote_addr) {
        if (StringUtils.isNotEmpty(x_forwarded_for)) {
            return x_forwarded_for.split(",")[0].trim();
        }
        return remote_addr;
    }
}

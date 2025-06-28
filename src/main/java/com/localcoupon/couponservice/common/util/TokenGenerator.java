package com.localcoupon.couponservice.common.util;

import java.util.UUID;
//TODO : 추후 토큰 생성 방식 변경 예정
public class TokenGenerator {
    public static String createSessionToken() {
        return UUID.randomUUID().toString();
    }
}

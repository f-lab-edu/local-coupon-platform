package com.localcoupon.couponservice.coupon.service;

import java.time.LocalDateTime;

public interface QrTokenService {
    String generateQrToken(Long issuedCouponId, LocalDateTime validStartTime, LocalDateTime validEndTime);
    String uploadQrImage(String qrToken);
    boolean isTokenValid(String token);
}
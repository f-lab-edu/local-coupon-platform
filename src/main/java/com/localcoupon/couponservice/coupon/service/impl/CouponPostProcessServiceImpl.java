package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponPostProcessServiceImpl implements CouponPostProcessService {
    private final QrTokenService qrTokenService;
    private final CouponMailService couponMailService;
    private final IssuedCouponRepository issuedCouponRepository;

    @Async
    public void sendQrCouponToUser(User user, IssuedCoupon issuedCoupon) {
        try {
            // 1. QR 토큰 생성
            CouponPeriod couponValidPeriod = issuedCoupon.getCoupon().getValidPeriod();
            String qrToken = qrTokenService.generateQrToken(issuedCoupon.getId(),
                    couponValidPeriod.getStart(),
                    couponValidPeriod.getEnd()
            );

            // 2. Cloudinary 업로드
            String qrImageUrl = qrTokenService.uploadQrImage(qrToken);

            // 3. 이메일 발송
            couponMailService.sendCouponEmail(user.getEmail(), issuedCoupon.getCoupon().getTitle(), qrImageUrl);

            // 4. 발급 쿠폰 업데이트
            issuedCouponRepository.save(issuedCoupon.postProcess(qrToken, qrImageUrl));

            log.info("[CouponPostProcessService] 후처리 완료 issuedCouponId={}, userId={}", issuedCoupon.getId(), user.getEmail());
        } catch (Exception e) {
            // TODO 실패 시 처리 방안
            log.error("[CouponPostProcessService] 후처리 실패 issuedCouponId={}, userId={}", issuedCoupon.getId(), user.getEmail(), e);
        }
    }
}

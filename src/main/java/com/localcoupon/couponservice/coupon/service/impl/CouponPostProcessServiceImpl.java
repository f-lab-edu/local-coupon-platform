package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.util.TimeProvider;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserExcpetion;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponPostProcessServiceImpl implements CouponPostProcessService {
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;
    private final QrTokenService qrTokenService;
    private final CouponMailService couponMailService;
    private final TimeProvider timeProvider;

    @Async
    public CompletableFuture<Result> sendQrCouponToUser(Long userId, Coupon coupon) {
        try {
            // 1.IssuedCoupon 발급을 위해 User 객체 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserExcpetion(UserErrorCode.USER_NOT_FOUND));

            // 2.QR 토큰 생성
            String qrToken = qrTokenService.generateQrToken(
                    coupon.getId(), coupon.getCouponValidStartTime(), coupon.getCouponValidEndTime()
            );

            // 3.Cloudinary에 QR 이미지 업로드
            String qrImageUrl = qrTokenService.uploadQrImage(qrToken);

            // 4. 쿠폰 발급 처리
            IssuedCoupon issuedCoupon = issuedCouponRepository.save(coupon.issue(user, qrImageUrl, timeProvider.now()));

            // 5. 유저에게 이메일 발송
            couponMailService.sendCouponEmail(user.getEmail(), coupon.getTitle(), issuedCoupon.getQrToken());

            log.info("[CouponPostProcessService] 쿠폰 발급 후처리 완료 couponId={}, userId={}", coupon.getId(), userId);
            return CompletableFuture.completedFuture(Result.SUCCESS);

        } catch (Exception ex) {
            log.error("[CouponPostProcessService] 쿠폰 발급 후처리 실패 couponId={}, userId={}", coupon.getId(), userId, ex);
            return CompletableFuture.completedFuture(Result.FAIL);
        }
    }
}

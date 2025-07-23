package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.CouponPostProcessDto;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponPostProcessServiceImpl implements CouponPostProcessService {
    private final QrTokenService qrTokenService;
    private final CouponMailService couponMailService;
    private final IssuedCouponRepository issuedCouponRepository;

    @Async
    @Transactional
    public CompletableFuture<Result> sendQrCouponToUser(CouponPostProcessDto couponPostProcessDto, IssuedCoupon issuedCoupon) {
        try {
            // 1.QR 토큰 생성
            String qrToken = qrTokenService.generateQrToken(
                    couponPostProcessDto.issuedCouponId(), couponPostProcessDto.issuedCouponValidStartTime(), couponPostProcessDto.issuedCouponValidEndTime()
            );

            // 2.Cloudinary에 QR 이미지 업로드
            String qrImageUrl = qrTokenService.uploadQrImage(qrToken);

            // 3. 유저에게 이메일 발송
            Result result = couponMailService.sendCouponEmail(couponPostProcessDto.userEmail(), couponPostProcessDto.couponTitle(), qrImageUrl);

            // 4. issuedCoupon 후처리
            IssuedCoupon updatedIssuedCoupon = issuedCouponRepository.save(issuedCoupon);

            log.info("[CouponPostProcessService] 쿠폰 발급 후처리 완료 issuedCouponId={}, userId={}", couponPostProcessDto.issuedCouponId(), couponPostProcessDto.userId());
            return CompletableFuture.completedFuture(Result.SUCCESS);

        } catch (Exception e) {
            log.error("[CouponPostProcessService] 쿠폰 발급 후처리 실패 issuedCouponId={}, userId={}", couponPostProcessDto.issuedCouponId(), couponPostProcessDto.userId(), e);
            return CompletableFuture.completedFuture(Result.FAIL);
        }
    }
}

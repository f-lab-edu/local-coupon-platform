package com.localcoupon.couponservice.coupon.event;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.coupon.service.impl.CouponMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CouponIssuedEventHandler {

  private final IssuedCouponRepository issuedCouponRepository;
  private final QrTokenService qrTokenService;
  private final CouponMailService couponMailService;

  @Async("couponIssueAsync") // 비동기 스레드풀에서 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 커밋 후 실행
  @Transactional // 리스너 내부 DB 작업을 하나의 트랜잭션으로
  public void handle(CouponIssuedEvent e) {
    // 1. 커밋 이후이므로 안전하게 재조회 (영속 엔티티 확보)
    IssuedCoupon issued = issuedCouponRepository.findById(e.issuedCouponId())
        .orElseThrow();

    // 2. 이미 처리된 발급이면 스킵
    if (issued.getQrToken() != null) {
      log.info("[CouponPostProcess] already processed issuedId={}", e.issuedCouponId());
      return;
    }

    // QR 토큰 생성 + 업로드
    String qrToken = qrTokenService.generateQrToken(
        e.issuedCouponId(), e.validStart(), e.validEnd());
    String qrImageUrl = qrTokenService.uploadQrImage(qrToken);

    // 3.. 발급 쿠폰 업데이트 (변경감지)
    issued.postProcess(qrToken, qrImageUrl);

    // 4) 이메일 발송
    couponMailService.sendCouponEmail(e.userEmail(), e.couponTitle(), qrImageUrl);

    log.info("[CouponPostProcess] done issuedId={}", e.issuedCouponId());
  }
}

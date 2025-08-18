package com.localcoupon.couponservice.coupon.event;

import com.localcoupon.couponservice.coupon.dto.QrUpserResult;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import com.localcoupon.couponservice.coupon.service.impl.CouponMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssuedEventHandler {

  private final CouponIssueService couponIssueService;
  private final CouponMailService couponMailService;

  @Async("couponIssueAsync")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(CouponIssuedEvent e) {
    try {
      QrUpserResult result = couponIssueService.postProcessQrCode(e);
      couponMailService.sendCouponEmail(e.userEmail(), e.couponTitle(), result.qrImageUrl());
    } catch (Exception ex) {
      log.error("[CouponPostProcess] failed event={}", e, ex);
    }
  }
}


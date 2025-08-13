package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.event.CouponIssuedEvent;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class CouponIssueServiceImpl implements CouponIssueService {

  private final CouponRedisRepository couponRedisRepository;
  private final IssuedCouponRepository issuedCouponRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public Coupon saveCouponForOpen(Coupon coupon) {
    String key = "coupon:open:" + coupon.getId();

    long ttlSeconds = Duration.between(
        LocalDateTime.now(),
        coupon.getValidPeriod().getEnd().plusDays(1)
    ).getSeconds();

    couponRedisRepository.saveData(
        key,
        coupon.getTotalCount(),
        Duration.ofSeconds(ttlSeconds)
    );
    return coupon;
  }

  @Override
  public boolean isCouponOpen(Long couponId) {
    String key = "coupon:open:" + couponId;
    return couponRedisRepository.exists(key);
  }

  @Override
  public int decreaseCouponStock(Long couponId) {
    String lockKey = "coupon:lock:" + couponId;
    String dataKey = "coupon:open:" + couponId;

    if (!isCouponOpen(couponId)) {
      throw new UserCouponException(UserCouponErrorCode.ENDED_COUPON_ISSUE);
    }

    return couponRedisRepository.executeWithLock(
        lockKey,
        2000,
        5000,
        () -> couponRedisRepository.decreaseCouponStock(dataKey)
            .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON))
    );
  }

  @Override
  public int increaseCouponStock(Long couponId) {
    String lockKey = "coupon:lock:" + couponId;
    String dataKey = "coupon:open:" + couponId;

    return couponRedisRepository.executeWithLock(
        lockKey,
        2000,
        5000,
        () -> couponRedisRepository.increaseCouponStock(dataKey).orElse(CouponStock.INIT.getValue())
    );
  }

  @Override
  @Transactional
  public Result processCouponIssue(Coupon coupon, Long userId, String userEmail) {
    try {
      // 1. 쿠폰 재고 처리
      decreaseCouponStock(coupon.getId());

      // 2. 발급 저장
      IssuedCoupon issuedCoupon = issuedCouponRepository.save(IssuedCoupon.of(userId, coupon));

      // 3. 커밋 후 실행될 이벤트 발행
      eventPublisher.publishEvent(new CouponIssuedEvent(
          issuedCoupon.getId(),
          userEmail,
          coupon.getValidPeriod().getStart(),
          coupon.getValidPeriod().getEnd(),
          coupon.getTitle()
      ));

      return Result.SUCCESS;
    } catch (UserCouponException e) {
      throw e;
    } catch (Exception e) {
      // 예외가 발생한 경우 롤백 작업 수행
      log.error("[ProcessCouponIssue] {} 쿠폰 발급에 실패하였습니다.", userId, e);
      handleFailedIssueCoupon(coupon.getId()); // 보상 트랜잭션(재고 복구)
      throw e;
    }
  }

  // 보상 트랜잭션: 쿠폰 발급 실패 시 재고를 복구하는 메서드
  private void handleFailedIssueCoupon(Long couponId) {
    try {
      increaseCouponStock(couponId); // 재고 복구
    } catch (Exception ex) {
      //TODO 재고 복구가 실패하면 로그를 남기고 추가적인 처리 필요
      log.error("[handleFailedIssueCoupon] 쿠폰 재고 복구 실패", ex);
    }
  }
}

package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

class CouponRedissonServiceImplTest {

  private final Long couponId = 1L;
  private final String redisKey = "coupon:open:" + couponId;
  private final String lockKey = "coupon:lock:" + couponId;
  @Mock
  private CouponRedisRepository redisCouponRepository;
  @Spy
  @InjectMocks
  private CouponIssueServiceImpl couponIssueService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("쿠폰을 Redis에 저장한다 - 저장 후 Coupon 반환")
  void saveCouponForOpen_shouldSaveCouponAndReturnCoupon() {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId); //id db생성값

    when(redisCouponRepository.getValue(redisKey, Integer.class))
        .thenReturn(Optional.of(coupon.getTotalCount()));

    // when
    Coupon savedCoupon = couponIssueService.saveCouponForOpen(coupon);

    // then
    assertThat(savedCoupon).isNotNull();
    assertThat(savedCoupon.getId()).isEqualTo(couponId);
    assertThat(savedCoupon.getTotalCount()).isEqualTo(coupon.getTotalCount());

    Optional<Integer> savedStock = redisCouponRepository.getValue(redisKey, Integer.class);
    assertThat(savedStock).isPresent();
    assertThat(savedStock.get()).isEqualTo(coupon.getTotalCount());
  }

  @Test
  @DisplayName("쿠폰 오픈 상태 확인 - Redis key 존재 시 true 반환")
  void isCouponOpen_shouldReturnTrue_whenKeyExists() {
    // given
    given(redisCouponRepository.exists(redisKey)).willReturn(true);

    // when
    boolean result = couponIssueService.isCouponOpen(couponId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("쿠폰 오픈 상태 확인 - Redis key 없으면 false 반환")
  void isCouponOpen_shouldReturnFalse_whenKeyDoesNotExist() {
    // given
    when(redisCouponRepository.exists(redisKey)).thenReturn(false);

    // when
    boolean result = couponIssueService.isCouponOpen(couponId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("쿠폰 재고 감소 - 재고 부족 시 예외 발생")
  void decreaseCouponStock_shouldThrowException_whenSoldOut() {
    // given
    when(couponIssueService.isCouponOpen(couponId)).thenReturn(true);
    when(redisCouponRepository.executeWithLock(
        eq(lockKey),
        anyLong(),
        anyLong(),
        any()
    )).thenThrow(new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON));

    // when & then
    assertThrows(UserCouponException.class,
        () -> couponIssueService.decreaseCouponStock(couponId));
  }

  @Test
  @DisplayName("쿠폰 재고 감소 - 성공 시 새로운 재고 반환")
  void decreaseCouponStock_shouldReturnStock_whenAvailable() {
    given(couponIssueService.isCouponOpen(couponId)).willReturn(true);

    when(redisCouponRepository.executeWithLock(
        eq(lockKey),
        anyLong(),
        anyLong(),
        any()
    )).thenReturn(9);

    // when
    int newStock = couponIssueService.decreaseCouponStock(couponId);

    // then
    assertThat(newStock).isEqualTo(9);
  }

  private Coupon createCoupon() {

    return new Coupon(
        CouponScope.LOCAL,
        "Test Coupon",
        "Coupon description",
        10,
        0,
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7)),
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7)),
        1L
    );
  }
}

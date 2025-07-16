package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.CouponData;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CouponClosedBatchTest {
    @Mock
    private CouponRedisRepository couponRedisRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private RedisProperties redisProperties;
    @InjectMocks
    private CouponClosedBatch couponClosedBatch;

    Clock fixedClock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisProperties.couponOpenPrefix()).thenReturn("coupon:open:");
        fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 7, 16, 10, 0)
                        .atZone(ZoneId.systemDefault())
                        .toInstant(),
                ZoneId.systemDefault()
        );
        couponClosedBatch = new CouponClosedBatch(couponRepository, couponRedisRepository, redisProperties, fixedClock);
    }

    @Test
    @DisplayName("배치에서 쿠폰 오픈 타임 종료 시 정상 삭제처리한다")
    void deleteExpiredCoupons() {
        //given
        List<Coupon> expiredCoupons = List.of(CouponData.expiredCoupon(1L, fixedClock));

        when(couponRepository.findByCouponIssueEndTimeBefore(any()))
                .thenReturn(expiredCoupons);

        when(couponRedisRepository.getValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of("10"));


        when(couponRedisRepository.deleteData(anyString()))
                .thenReturn(true);
        //when
        couponClosedBatch.deleteExpiredCoupons();

        // then
        for (Coupon coupon : expiredCoupons) {
            Coupon expiredCoupon = coupon.syncIssuedCount(3);
            assertThat(coupon.getIssuedCount()).isEqualTo(expiredCoupon.getIssuedCount());
        }
        verify(couponRedisRepository, times(1))
                .deleteData(anyString());
    }

    @Test
    @DisplayName("하나의 쿠폰 처리 중 예외가 발생해도 다음 쿠폰을 계속 처리한다")
    void deleteExpiredCoupons_exceptionHandling() {
        // given
        List<Coupon> coupons = List.of(
                CouponData.expiredCoupon(1L, fixedClock),
                CouponData.expiredCoupon(2L, fixedClock)
        );

        when(couponRepository.findByCouponIssueEndTimeBefore(any()))
                .thenReturn(coupons);

        when(redisProperties.couponOpenPrefix()).thenReturn("coupon:open:");

        //1번 쿠폰 레디스 에러
        when(couponRedisRepository.getValue("coupon:open:" + 1L, String.class))
                .thenThrow(new RuntimeException("Redis error"));

        //2번 쿠폰 가져오기 성공
        when(couponRedisRepository.getValue("coupon:open:" + 2L, String.class))
                .thenReturn(Optional.of("10"));

        when(couponRedisRepository.deleteData(anyString()))
                .thenReturn(true);

        // when
        couponClosedBatch.deleteExpiredCoupons();

        // then
        //2개니까 2번 조회가 일어나야하고 1번만 삭제되어야 한다.
        verify(couponRedisRepository, times(2))
                .getValue(anyString(), eq(String.class));

        verify(couponRedisRepository, times(1))
                .deleteData("coupon:open:" + 2L);
    }
}

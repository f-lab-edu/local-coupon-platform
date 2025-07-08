package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.infra.redis.RedisCouponRepository;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_LOCK_PREFIX;
import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class CouponRedissonServiceTest {

    @Mock
    private RedisCouponRepository redisCouponRepository;

    @InjectMocks
    private CouponRedissonServiceImpl couponCacheService;

    private final Long couponId = 1L;
    private final String redisKey = COUPON_OPEN_PREFIX + couponId;
    private final String lockKey = COUPON_LOCK_PREFIX + couponId;

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

        when(redisCouponRepository.getData(redisKey))
                .thenReturn(Optional.of(coupon.getTotalCount()));

        // when
        Coupon savedCoupon = couponCacheService.saveCouponForOpen(coupon);

        // then
        assertThat(savedCoupon).isNotNull();
        assertThat(savedCoupon.getId()).isEqualTo(couponId);
        assertThat(savedCoupon.getTotalCount()).isEqualTo(coupon.getTotalCount());

        Optional<Integer> savedStock = redisCouponRepository.getData(redisKey);
        assertThat(savedStock).isPresent();
        assertThat(savedStock.get()).isEqualTo(coupon.getTotalCount());
    }

    @Test
    @DisplayName("쿠폰 오픈 상태 확인 - Redis key 존재 시 true 반환")
    void isCouponOpen_shouldReturnTrue_whenKeyExists() {
        // given
        when(redisCouponRepository.exists(redisKey)).thenReturn(true);

        // when
        boolean result = couponCacheService.isCouponOpen(couponId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠폰 오픈 상태 확인 - Redis key 없으면 false 반환")
    void isCouponOpen_shouldReturnFalse_whenKeyDoesNotExist() {
        // given
        when(redisCouponRepository.exists(redisKey)).thenReturn(false);

        // when
        boolean result = couponCacheService.isCouponOpen(couponId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("쿠폰 재고 감소 - 재고 부족 시 예외 발생")
    void decreaseCouponStock_shouldThrowException_whenSoldOut() {
        // given
        when(redisCouponRepository.executeWithLock(
                eq(lockKey),
                anyLong(),
                anyLong(),
                any()
        )).thenThrow(new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON));

        // when & then
        assertThrows(UserCouponException.class,
                () -> couponCacheService.decreaseCouponStock(couponId));
    }

    @Test
    @DisplayName("쿠폰 재고 감소 - 성공 시 새로운 재고 반환")
    void decreaseCouponStock_shouldReturnStock_whenAvailable() {
        // given executeWithLock Stub 객체 생성
        when(redisCouponRepository.executeWithLock(
                eq(lockKey),
                anyLong(),
                anyLong(),
                any()
        )).thenReturn(9);

        // when
        int newStock = couponCacheService.decreaseCouponStock(couponId);

        // then
        assertThat(newStock).isEqualTo(9);
    }

    private Coupon createCoupon() {
        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        KakaoGeocodeInfoDto geoCodeInfo = new KakaoGeocodeInfoDto(
                "110105",
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276")
        );
        return new Coupon(
                CouponScope.LOCAL,
                "Test Coupon",
                "Coupon description",
                10,
                0,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                Store.from(request,geoCodeInfo, 1L)
        );
    }
}

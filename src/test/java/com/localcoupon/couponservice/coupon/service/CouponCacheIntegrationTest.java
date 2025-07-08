package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CouponRedissonServiceIntegrationTest {

    @Autowired
    @Qualifier("couponRedissonServiceImpl")
    private CouponCacheService couponRedissonServiceImpl;

    @Autowired
    private RedissonClient redissonClient;

    private final Long couponId = 1L;
    private final String redisKey = COUPON_OPEN_PREFIX + couponId;

    @AfterEach
    void tearDown() {
        // 테스트 끝나면 Redis 비우기
        redissonClient.getKeys().flushall();
    }

    @Test
    @DisplayName("쿠폰 저장 후 Redis에 잘 들어갔는지 확인한다.")
    void saveCouponForOpen_shouldSaveCouponInRedis() {
        // given
        Coupon coupon = createCoupon();
        ReflectionTestUtils.setField(coupon, "id", couponId);

        // when
        Coupon savedCoupon = couponRedissonServiceImpl.saveCouponForOpen(coupon);

        // then
        assertThat(savedCoupon).isNotNull();
        assertThat(savedCoupon.getId()).isEqualTo(couponId);

        Integer savedStock = (Integer) redissonClient.getBucket(redisKey).get();
        assertThat(savedStock).isEqualTo(coupon.getTotalCount());
    }

    @Test
    @DisplayName("쿠폰이 Redis에 존재할 때 isCouponOpen은 true를 반환한다.")
    void isCouponOpen_shouldReturnTrue_whenKeyExists() {
        // given
        Coupon coupon = createCoupon();
        ReflectionTestUtils.setField(coupon, "id", couponId);
        couponRedissonServiceImpl.saveCouponForOpen(coupon);

        // when
        boolean exists = couponRedissonServiceImpl.isCouponOpen(couponId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("쿠폰이 Redis에 존재하지 않을 때 isCouponOpen은 false를 반환한다.")
    void isCouponOpen_shouldReturnFalse_whenKeyNotExists() {
        // when
        boolean exists = couponRedissonServiceImpl.isCouponOpen(999L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("쿠폰 재고 감소 시 재고가 줄어들고 새로운 재고가 반환된다.")
    void decreaseCouponStock_shouldDecreaseStock() {
        // given
        Coupon coupon = createCoupon();
        ReflectionTestUtils.setField(coupon, "id", couponId);
        couponRedissonServiceImpl.saveCouponForOpen(coupon);

        // when
        int remainingStock = couponRedissonServiceImpl.decreaseCouponStock(couponId);

        // then
        assertThat(remainingStock).isEqualTo(coupon.getTotalCount() - 1);
    }

    @Test
    @DisplayName("재고가 없으면 decreaseCouponStock은 예외를 던진다.")
    void decreaseCouponStock_shouldThrowException_whenSoldOut() {
        // given
        Coupon coupon = createCoupon();
        ReflectionTestUtils.setField(coupon, "id", couponId);
        couponRedissonServiceImpl.saveCouponForOpen(coupon);

        // 재고를 0으로 만들어서 매진 상태로
        for (int i = 0; i < coupon.getTotalCount(); i++) {
            couponRedissonServiceImpl.decreaseCouponStock(couponId);
        }

        // when & then
        assertThrows(UserCouponException.class,
                () -> couponRedissonServiceImpl.decreaseCouponStock(couponId));
    }
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

    private Coupon createCoupon() {
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
                (Store.from(request,geoCodeInfo, 1L))
        );
    }

    @Test
    @DisplayName("쿠폰 재고 감소 동시성 테스트 - 중복 발급 방지")
    void decreaseCouponStock_concurrentTest() throws InterruptedException {
        // given
        Coupon coupon = createCoupon();
        ReflectionTestUtils.setField(coupon, "id", couponId);
        couponRedissonServiceImpl.saveCouponForOpen(coupon);

        int threadCount = 20;  // 기존 동시 요청 수
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Future<Integer>> results = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            Future<Integer> future = executor.submit(() -> {
                try {
                    return couponRedissonServiceImpl.decreaseCouponStock(couponId);
                } catch (UserCouponException e) {
                    return -1;
                } finally {
                    latch.countDown();
                }
            });
            results.add(future);
        }

        latch.await();
        //latch 대기

        // then
        int successCount = 0;
        int soldOutCount = 0;

        for (Future<Integer> future : results) {
            try {
                int result = future.get();
                if (result >= 0) {
                    successCount++;
                } else {
                    soldOutCount++;
                }
            } catch (ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("재고 감소 성공 수: " + successCount);
        System.out.println("매진 응답 수: " + soldOutCount);

        assertThat(successCount).isEqualTo(coupon.getTotalCount());
        assertThat(soldOutCount).isEqualTo(threadCount - coupon.getTotalCount());
    }

}

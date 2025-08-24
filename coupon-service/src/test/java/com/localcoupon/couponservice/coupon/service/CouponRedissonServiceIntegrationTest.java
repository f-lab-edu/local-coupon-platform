package com.localcoupon.couponservice.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("local")
class CouponRedissonServiceIntegrationTest {

  private final Long couponId = 1L;
  @Autowired
  @Qualifier("couponIssueServiceImpl")
  private CouponIssueService couponIssueService;
  @Autowired
  private RedissonClient redissonClient;
  private String openPrefix;   // e.g. "coupon:open:"
  private String lockPrefix;   // e.g. "coupon:lock:"
  private String dataKey;      // e.g. "coupon:open:1"
  private String lockKey;      // e.g. "coupon:open:1"

  @BeforeEach
  void setUp() {
    openPrefix = "coupon:open:";
    lockPrefix = "coupon:lock:";

    dataKey = openPrefix + couponId;
    lockKey = lockPrefix + couponId;
  }

  @AfterEach
  void tearDown() {
    // 레디스 비우기
    redissonClient.getKeys().flushall();
  }

  @Test
  @DisplayName("쿠폰 저장 후 Redis에 잘 들어갔는지 확인한다.")
  void saveCouponForOpen_shouldSaveCouponInRedis() {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId);

    // when
    Coupon savedCoupon = couponIssueService.saveCouponForOpen(coupon);

    // then
    assertThat(savedCoupon).isNotNull();
    assertThat(savedCoupon.getId()).isEqualTo(couponId);

    RBucket<Object> bucket = redissonClient.getBucket(dataKey);
    Object raw = bucket.get();

    int savedStock = toInt(raw);
    assertThat(savedStock).isEqualTo(coupon.getTotalCount());
  }

  @Test
  @DisplayName("쿠폰이 Redis에 존재할 때 isCouponOpen은 true를 반환한다.")
  void isCouponOpen_shouldReturnTrue_whenKeyExists() {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId);
    couponIssueService.saveCouponForOpen(coupon);

    // when
    boolean exists = couponIssueService.isCouponOpen(couponId);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("쿠폰이 Redis에 존재하지 않을 때 isCouponOpen은 false를 반환한다.")
  void isCouponOpen_shouldReturnFalse_whenKeyNotExists() {
    // when
    boolean exists = couponIssueService.isCouponOpen(999L);

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("쿠폰 재고 감소 시 재고가 줄어들고 새로운 재고가 반환된다.")
  void decreaseCouponStock_shouldDecreaseStock() {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId);
    couponIssueService.saveCouponForOpen(coupon);

    // when
    int remainingStock = couponIssueService.decreaseCouponStock(couponId);

    // then
    assertThat(remainingStock).isEqualTo(coupon.getTotalCount() - 1);
  }

  @Test
  @DisplayName("재고가 없으면 decreaseCouponStock은 예외를 던진다.")
  void decreaseCouponStock_shouldThrowException_whenSoldOut() {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId);
    couponIssueService.saveCouponForOpen(coupon);

    // 재고를 0으로 만들어 매진 상태로
    for (int i = 0; i < coupon.getTotalCount(); i++) {
      couponIssueService.decreaseCouponStock(couponId);
    }

    // when & then
    assertThrows(UserCouponException.class,
        () -> couponIssueService.decreaseCouponStock(couponId));
  }

  @Test
  @DisplayName("쿠폰 재고 감소 동시성 테스트 - 중복 발급 방지")
  void decreaseCouponStock_concurrentTest() throws InterruptedException {
    // given
    Coupon coupon = createCoupon();
    ReflectionTestUtils.setField(coupon, "id", couponId);
    couponIssueService.saveCouponForOpen(coupon);

    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    List<Future<Integer>> results = new ArrayList<>();

    // when
    for (int i = 0; i < threadCount; i++) {
      Future<Integer> f = executor.submit(() -> {
        try {
          return couponIssueService.decreaseCouponStock(couponId);
        } catch (UserCouponException e) {
          return -1; // 매진 등 예외는 -1로 기록
        } finally {
          latch.countDown();
        }
      });
      results.add(f);
    }

    latch.await();
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);

    // then
    int successCount = 0;
    int soldOutCount = 0;

    for (Future<Integer> f : results) {
      try {
        int v = f.get();
        if (v >= 0) {
          successCount++;
        } else {
          soldOutCount++;
        }
      } catch (ExecutionException e) {
        // 실패는 매진 케이스로 간주하지 않고 로깅만
        System.out.println("Execution error: " + e.getMessage());
      }
    }

    System.out.println("재고 감소 성공 수: " + successCount);
    System.out.println("매진 응답 수: " + soldOutCount);

    assertThat(successCount).isEqualTo(coupon.getTotalCount());
    assertThat(soldOutCount).isEqualTo(threadCount - coupon.getTotalCount());
  }

  private int toInt(Object raw) {
    if (raw == null) {
      return 0;
    }
    if (raw instanceof Integer i) {
      return i;
    }
    if (raw instanceof Long l) {
      return l.intValue();
    }
    if (raw instanceof String s) {
      return Integer.parseInt(s);
    }
    return Integer.parseInt(String.valueOf(raw));
  }

  private Coupon createCoupon() {
    return new Coupon(
        CouponScope.LOCAL,
        "Test Coupon",
        "Coupon description",
        10, // totalCount
        0,  // issuedCount
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7)), // valid
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7)), // issue
        1L
    );
  }
}

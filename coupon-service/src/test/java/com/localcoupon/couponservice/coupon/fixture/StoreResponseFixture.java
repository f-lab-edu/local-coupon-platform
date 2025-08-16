package com.localcoupon.couponservice.coupon.fixture;

import com.localcoupon.couponservice.coupon.common.contract.store.StoreCategory;
import com.localcoupon.couponservice.coupon.common.contract.store.StoreResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class StoreResponseFixture {

  private StoreResponseFixture() {
  }

  /**
   * 가장 흔한 샘플
   */
  public static StoreResponseDto sample() {
    return sample(
        10L,
        "로컬쿠폰 가맹점",
        "서울시 강남구 테헤란로 123",
        StoreCategory.FOOD,                // 프로젝트 enum에 맞게 변경 가능
        new BigDecimal("37.498095"),
        new BigDecimal("127.027610"),
        "02-1234-5678",
        "강남역 근처 맛집",
        "https://cdn.example.com/store/10.jpg",
        LocalDateTime.now()
    );
  }

  /**
   * 원하는 값으로 쉽게 만들 수 있는 오버로드
   */
  public static StoreResponseDto sample(
      Long id,
      String name,
      String address,
      StoreCategory category,
      BigDecimal latitude,
      BigDecimal longitude,
      String phoneNumber,
      String description,
      String imageUrl,
      LocalDateTime createdAt
  ) {
    return new StoreResponseDto(
        id,
        name,
        address,
        category,
        latitude,
        longitude,
        phoneNumber,
        description,
        imageUrl,
        createdAt
    );
  }
}

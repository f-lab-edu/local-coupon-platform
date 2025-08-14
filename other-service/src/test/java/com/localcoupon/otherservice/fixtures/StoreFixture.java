package com.localcoupon.otherservice.fixtures;

import com.localcoupon.otherservice.store.entity.Store;
import com.localcoupon.otherservice.store.enums.StoreCategory;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public final class StoreFixture {

  private StoreFixture() {
  }

  /**
   * 기본 Store 한 건
   */
  public static Store store() {
    return Store.builder()
        .id(1L)
        .ownerId(10L)
        .name("스타벅스")
        .address("서울시 송파구 법원로 55")
        .category(StoreCategory.CAFE)
        .phoneNumber("02-1234-5678")
        .description("매장 설명입니다.")
        .imageUrl("http://example.com/image.jpg")
        .latitude(new BigDecimal("37.4979"))
        .longitude(new BigDecimal("127.0276"))
        .build();
  }

  /**
   * 빌더 오버라이드로 일부만 바꾸고 싶을 때
   */
  public static Store store(UnaryOperator<Store.StoreBuilder> overrides) {
    Store.StoreBuilder base = Store.builder()
        .id(1L)
        .ownerId(10L)
        .name("스타벅스")
        .address("서울시 송파구 법원로 55")
        .category(StoreCategory.CAFE)
        .phoneNumber("02-1234-5678")
        .description("매장 설명입니다.")
        .imageUrl("http://example.com/image.jpg")
        .latitude(new BigDecimal("37.4979"))
        .longitude(new BigDecimal("127.0276"));
    return overrides.apply(base).build();
  }
}

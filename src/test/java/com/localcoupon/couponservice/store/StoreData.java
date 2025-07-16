package com.localcoupon.couponservice.store;

import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;

import java.math.BigDecimal;

public class StoreData {

    public static Store defaultStore() {
        return Store.builder()
                .id(1L)
                .ownerId(1L)
                .name("테스트 스토어")
                .address("서울시 송파구")
                .latitude(new BigDecimal("36.5"))
                .longitude(new BigDecimal("127.5"))
                .regionCode("23156")
                .phoneNumber("010-1234-5678")
                .category(StoreCategory.CAFE)
                .build();
    }
}


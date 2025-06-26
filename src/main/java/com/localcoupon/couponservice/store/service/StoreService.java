package com.localcoupon.couponservice.store.service;

import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;

import java.util.List;

public interface StoreService {

    StoreResponseDto registerStore(StoreRequestDto request);

    List<StoreResponseDto> getMyStores();
}

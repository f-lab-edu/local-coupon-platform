package com.localcoupon.couponservice.store.service;

import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;

import java.util.List;

public interface StoreService {

    StoreResponseDto registerStore(StoreRequestDto request, String userEmail);

    List<StoreResponseDto> getMyStores(String userEmail);
    List<StoreResponseDto> getStoresNearby(UserStoreSearchRequestDto request);

}

package com.localcoupon.otherservice.store.service;

import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.otherservice.store.dto.response.StoreResponseDto;

import java.util.List;

public interface StoreService {

    StoreResponseDto registerStore(StoreRequestDto request, Long userId);

    List<StoreResponseDto> getMyStores(Long userId);
    List<StoreResponseDto> getStoresNearby(UserStoreSearchRequestDto request);

}

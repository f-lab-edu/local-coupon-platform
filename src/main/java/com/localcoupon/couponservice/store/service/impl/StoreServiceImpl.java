package com.localcoupon.couponservice.store.service.impl;

import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.service.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Override
    public StoreResponseDto registerStore(StoreRequestDto request) {
        return null;
    }

    @Override
    public List<StoreResponseDto> getMyStores() {
        return List.of();
    }
}

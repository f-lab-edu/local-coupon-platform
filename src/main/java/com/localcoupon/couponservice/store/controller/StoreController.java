package com.localcoupon.couponservice.store.controller;

import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.API_Prefix.API_V1 + ApiMapping.STORE)
public class StoreController {

    private final StoreService storeService;

    // 🧾 점주 | POST | /api/stores | 내 매장 등록
    @PostMapping
    public SuccessResponse<StoreResponseDto> registerStore(@RequestBody StoreRequestDto request) {
        StoreResponseDto response = storeService.registerStore(request);
        return SuccessResponse.of(response);
    }

    // 🧾 점주 | GET | /api/stores/my | 내 매장 목록
    @GetMapping("/my")
    public SuccessResponse<List<StoreResponseDto>> getMyStores() {
        String ownerEmail = AuthContextHolder.getUserKey();
        List<StoreResponseDto> stores = storeService.getMyStores();
        return SuccessResponse.of(stores);
    }
}

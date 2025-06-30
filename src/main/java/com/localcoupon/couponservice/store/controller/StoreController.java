package com.localcoupon.couponservice.store.controller;

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
@RequestMapping(ApiMapping.STORE)
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public SuccessResponse<StoreResponseDto> registerStore(@RequestBody StoreRequestDto request) {
        StoreResponseDto response = storeService.registerStore(request);
        return SuccessResponse.of(response);
    }

    @GetMapping("/my")
    public SuccessResponse<List<StoreResponseDto>> getMyStores() {
        List<StoreResponseDto> stores = storeService.getMyStores();
        return SuccessResponse.of(stores);
    }
}

package com.localcoupon.otherservice.store.controller;

import com.localcoupon.common.constants.ApiMapping;
import com.localcoupon.common.dto.response.SuccessResponse;
import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.otherservice.store.dto.response.StoreResponseDto;
import com.localcoupon.otherservice.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.STORE)
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public SuccessResponse<StoreResponseDto> registerStore(@RequestHeader("X-USER-ID") Long userId,
                                                           @RequestBody StoreRequestDto request) {
        StoreResponseDto response = storeService.registerStore(request, userId);
        return SuccessResponse.of(response);
    }

    @GetMapping("/my")
    public SuccessResponse<List<StoreResponseDto>> getMyStores(@RequestHeader("X-USER-ID") Long userId) {
        List<StoreResponseDto> stores = storeService.getMyStores(userId);
        return SuccessResponse.of(stores);
    }

    @GetMapping("/nearby")
    public SuccessResponse<List<StoreResponseDto>>  getStoresNearby(
            @ModelAttribute @Valid UserStoreSearchRequestDto request
    ) {
        List<StoreResponseDto> nearBystores = storeService.getStoresNearby(request);
        return SuccessResponse.of(nearBystores);
    }

}

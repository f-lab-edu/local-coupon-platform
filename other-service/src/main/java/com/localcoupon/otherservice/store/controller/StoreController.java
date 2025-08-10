package com.localcoupon.otherservice.store.controller;

import com.localcoupon.common.constants.ApiMapping;
import com.localcoupon.common.dto.response.SuccessResponse;
import com.localcoupon.otherservice.auth.security.CustomUserDetails;
import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.otherservice.store.dto.response.StoreResponseDto;
import com.localcoupon.otherservice.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.STORE)
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public SuccessResponse<StoreResponseDto> registerStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestBody StoreRequestDto request) {
        StoreResponseDto response = storeService.registerStore(request, userDetails.getId());
        return SuccessResponse.of(response);
    }

    @GetMapping("/my")
    public SuccessResponse<List<StoreResponseDto>> getMyStores( @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<StoreResponseDto> stores = storeService.getMyStores(userDetails.getId());
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

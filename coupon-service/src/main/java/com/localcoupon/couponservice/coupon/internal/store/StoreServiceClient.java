package com.localcoupon.couponservice.coupon.internal.store;

import com.localcoupon.couponservice.coupon.common.contract.store.StoreResponseDto;
import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "storeClient",
        url = "${services.other.base-url}"
)
public interface StoreServiceClient {

    @GetMapping("/stores/my")
    SuccessResponse<StoreResponseDto> getMyStore(@RequestHeader("X-USER-ID") Long userId);
}

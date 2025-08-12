package com.localcoupon.couponservice.coupon.internal.store;

import com.localcoupon.couponservice.coupon.internal.store.dto.StoreSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "storeClient"
)
public interface StoreServiceClient {

    @GetMapping("/api/v1/stores/my")
    StoreSummaryDto getMyStore(@RequestHeader("X-USER-ID") Long userId);
}

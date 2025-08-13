package com.localcoupon.couponservice.coupon.internal.user;

import com.localcoupon.couponservice.coupon.common.contract.store.StoreResponseDto;
import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// services.user.base-url=yaml에서 주입 (게이트웨이나 직접 주소)
@FeignClient(
        name = "userClient",
        url = "${services.other.base-url}"
)
public interface UserServiceClient {
    @GetMapping("/users/me")
    SuccessResponse<StoreResponseDto> getById(@RequestHeader("X-USER-ID") Long id);
}

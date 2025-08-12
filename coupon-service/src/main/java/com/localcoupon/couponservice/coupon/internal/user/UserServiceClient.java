package com.localcoupon.couponservice.coupon.internal.user;

import com.localcoupon.couponservice.coupon.internal.user.dto.UserSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// services.user.base-url=yaml에서 주입 (게이트웨이나 직접 주소)
@FeignClient(
        name = "userClient"
)
public interface UserServiceClient {
    @GetMapping("/api/v1/users/me")
    UserSummaryDto getById(@RequestHeader("X-USER-ID") Long id);
}

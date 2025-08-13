package com.localcoupon.couponservice.coupon.controller;


import com.localcoupon.common.constants.ApiMapping;
import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.annotation.PreventDuplicateRequest;
import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER_COUPON_BASE)
public class UserCouponController {

  private final UserCouponService userCouponService;

  @PreventDuplicateRequest
  @PostMapping("/{couponId}/issue")
  public SuccessResponse<Result> issueCoupon(@RequestHeader("X-USER-ID") Long userId,
      @RequestHeader("X-USER-EMAIL") String userEmail,
      @PathVariable("couponId") Long couponId) {
    Result result = userCouponService.issueCoupon(userId, couponId, userEmail);
    return SuccessResponse.of(result);
  }

  @GetMapping
  public SuccessResponse<List<UserIssuedCouponResponseDto>> getMyCoupons() {
    List<UserIssuedCouponResponseDto> coupons = userCouponService.getUserCoupons();
    return SuccessResponse.of(coupons);
  }
}


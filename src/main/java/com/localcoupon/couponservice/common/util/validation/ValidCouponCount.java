package com.localcoupon.couponservice.common.util.validation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Min(value=1, message="쿠폰의 최소 개수는 1개부터 가능합니다.")
@Max(value=1000, message="쿠폰의 최대 개수는 1000개까지 가능합니다.")
public @interface ValidCouponCount {
}

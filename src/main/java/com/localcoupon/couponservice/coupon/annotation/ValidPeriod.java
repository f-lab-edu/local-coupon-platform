package com.localcoupon.couponservice.coupon.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PeriodValidator.class)
public @interface ValidPeriod {
    String message() default "쿠폰 기간이 올바르지 않습니다."; //실패시 메시지
    Class<?>[] groups() default {}; // 검증 그룹
    Class<? extends Payload>[] payload() default {}; // 부가정보
}

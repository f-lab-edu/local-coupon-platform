package com.localcoupon.couponservice.common.util.validation.validator;

import com.localcoupon.couponservice.common.util.validation.ValidPeriod;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class PeriodValidator implements ConstraintValidator<ValidPeriod, CouponPeriod> {

    @Override
    public boolean isValid(CouponPeriod CouponPeriod, ConstraintValidatorContext constraintValidatorContext) {
        if(CouponPeriod == null) {
            return false;
        }
        LocalDateTime start = CouponPeriod.getStart();
        LocalDateTime end = CouponPeriod.getEnd();

        if (start == null || end == null) return false;
        if (end.isBefore(start)) return false;
        return true;
    }
}

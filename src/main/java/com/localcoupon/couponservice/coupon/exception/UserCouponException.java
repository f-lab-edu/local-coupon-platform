package com.localcoupon.couponservice.coupon.exception;

import com.localcoupon.couponservice.common.exception.BaseException;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;

public class UserCouponException extends BaseException {
    public UserCouponException(UserCouponErrorCode errorCode) {
        super(errorCode);
    }
}

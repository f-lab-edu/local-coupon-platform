package com.localcoupon.couponservice.user.exception;

import com.localcoupon.couponservice.common.exception.BaseException;
import com.localcoupon.couponservice.user.enums.UserErrorCode;

public class UserExcpetion extends BaseException {

    public UserExcpetion(UserErrorCode errorCode) {
        super(errorCode);
    }
}

package com.localcoupon.couponservice.user.exception;

import com.localcoupon.couponservice.common.exception.BaseException;
import com.localcoupon.couponservice.user.enums.UserErrorCode;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(UserErrorCode errorCode) {
        super(errorCode);
    }
}

package com.localcoupon.couponservice.user.exception;

import com.localcoupon.couponservice.global.exception.BaseException;
import com.localcoupon.couponservice.global.exception.ErrorCode;
import com.localcoupon.couponservice.user.enums.UserErrorCode;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(UserErrorCode errorCode) {
        super(errorCode);
    }
}

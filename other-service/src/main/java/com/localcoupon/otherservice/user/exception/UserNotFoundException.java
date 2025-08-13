package com.localcoupon.otherservice.user.exception;

import com.localcoupon.otherservice.common.exception.BaseException;
import com.localcoupon.otherservice.user.enums.UserErrorCode;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(UserErrorCode errorCode) {
        super(errorCode);
    }
}

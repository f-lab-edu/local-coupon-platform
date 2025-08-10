package com.localcoupon.otherservice.user.exception;

import com.localcoupon.common.exception.BaseException;
import com.localcoupon.otherservice.user.enums.UserErrorCode;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(UserErrorCode errorCode) {
        super(errorCode);
    }
}

package com.localcoupon.otherservice.user.exception;

import com.localcoupon.common.exception.BaseException;
import com.localcoupon.otherservice.user.enums.UserErrorCode;

public class UserExcpetion extends BaseException {

    public UserExcpetion(UserErrorCode errorCode) {
        super(errorCode);
    }
}

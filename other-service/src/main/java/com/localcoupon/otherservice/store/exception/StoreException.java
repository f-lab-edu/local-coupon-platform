package com.localcoupon.otherservice.store.exception;

import com.localcoupon.otherservice.common.exception.BaseException;
import com.localcoupon.otherservice.common.exception.ErrorCode;

public class StoreException extends BaseException {
    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}

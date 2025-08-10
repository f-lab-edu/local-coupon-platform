package com.localcoupon.otherservice.store.exception;

import com.localcoupon.common.exception.BaseException;
import com.localcoupon.common.exception.ErrorCode;

public class StoreException extends BaseException {
    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }
}

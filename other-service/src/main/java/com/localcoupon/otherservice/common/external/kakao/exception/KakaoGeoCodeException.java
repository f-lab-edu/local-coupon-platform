package com.localcoupon.otherservice.common.external.kakao.exception;


import com.localcoupon.otherservice.common.exception.BaseException;
import com.localcoupon.otherservice.common.exception.ErrorCode;

public class KakaoGeoCodeException extends BaseException {
    public KakaoGeoCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}

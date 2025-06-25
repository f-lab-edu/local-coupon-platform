package com.localcoupon.couponservice.global.util;

import com.localcoupon.couponservice.global.dto.LogContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    public static void error(LogContext logContext) {
        //언제 누가 어디서 무엇을 왜
        log.error("사용자: {}, IP: {}, 요청: {} {}, 스택 트레이스: ",
                logContext.userId(), logContext.ip(), logContext.method(), logContext.uri(), logContext.exception());
    }
}

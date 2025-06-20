package com.localcoupon.couponservice.global.exception;

import com.localcoupon.couponservice.global.CommonErrorCode;
import com.localcoupon.couponservice.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외(BaseException) 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.warn("[BaseException] {} - {}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    // 잘못된 인자, 검증 오류 등 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("[IllegalArgumentException] {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorCode.BAD_REQUEST));
    }
    // DB 에러 처리
    @ExceptionHandler({ SQLException.class, DataAccessException.class })
    public ResponseEntity<ErrorResponse> handleDatabaseException(Exception e) {
        log.error("[DB_ERROR] {}", e.getMessage(), e);
        return ResponseEntity
                .status(CommonErrorCode.DATABASE_ERROR.getStatus())
                .body(ErrorResponse.of(CommonErrorCode.DATABASE_ERROR));
    }

    // 알 수 없는 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[UNEXPECTED] {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(CommonErrorCode.BAD_REQUEST));
    }
}

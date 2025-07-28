package com.localcoupon.couponservice.common.exception;

import com.localcoupon.couponservice.common.dto.response.ErrorResponse;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String detailMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("[%s] %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(" | "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorCode.BAD_REQUEST, detailMessage));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(CommonErrorCode.HTTP_METHOD_NOT_ALLOWED, e.getMessage()));
    }
    // 엔티티 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(CommonErrorCode.ENTITY_NOT_FOUND_ERROR,e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(CommonErrorCode.NO_STATIC_RESOURCE_API, e.getMessage()));
    }


    // 커스텀 예외(BaseException) 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("[BUSINESS_ERROR] {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }

    // 잘못된 인자, 검증 오류 등 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorCode.BAD_REQUEST, e.getMessage()));
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
                .body(ErrorResponse.of(CommonErrorCode.SERVER_ERROR));
    }
}

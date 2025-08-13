package com.localcoupon.couponservice.coupon.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
    JSON_SERIALIZE_ERROR("직렬화에 실패했습니다.", HttpStatus.BAD_REQUEST),
    GEO_LOCATION_ERROR("위치 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ERROR("중복 요청은 불가능합니다.", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("잘못된 요청 입니다.", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND_ERROR("데이터가 없습니다.", HttpStatus.NOT_FOUND),
    SERVER_ERROR("서버 에러 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("데이터베이스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_OPERATION_ERROR("레디스 처리 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_EXCEPTION("알 수 없는 에러가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    QR_CREATE_OPERATION_ERROR("QR 코드 생성 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    CLOUDINARY_OPERATION_ERROR("Cloudinary 파일 업로드 오류", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    private CommonErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return this.name();
    }

}


package com.localcoupon.couponservice.common.dto.response;
import com.localcoupon.couponservice.common.util.StringUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessResponse<T> extends BaseResponse {
    private static final String SUCCESS_DEFAULT_MESSAGE = "요청이 성공적으로 처리되었습니다.";
    private final T data;

    private SuccessResponse(String message, HttpStatus status, T data) {
        super(true, message, status);
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(SUCCESS_DEFAULT_MESSAGE, HttpStatus.OK, data);
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        message = getMessage(message);
        return new SuccessResponse<>(message, HttpStatus.OK, data);
    }

    public static <T> SuccessResponse<T> of(String message, HttpStatus status, T data) {
        return new SuccessResponse<>(message, status, data);
    }
    private static String getMessage(String message) {
        if(StringUtils.isEmpty(message)) {
            return SUCCESS_DEFAULT_MESSAGE;
        }
        return message;
    }

}

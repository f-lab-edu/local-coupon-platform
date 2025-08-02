package com.localcoupon.couponservice.common.util.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Email(message = "이메일 형식이 올바르지 않습니다.")
@NotBlank(message = "이메일은 비워둘 수 없습니다.")
@Email
public @interface ValidEmail {
}

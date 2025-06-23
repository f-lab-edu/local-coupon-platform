package com.localcoupon.couponservice.user.dto.request;

public record SignUpRequestDto(String email, String password, String nickname, String address, String regionCode) {

}

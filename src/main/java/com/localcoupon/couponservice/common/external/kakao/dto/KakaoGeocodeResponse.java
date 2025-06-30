package com.localcoupon.couponservice.common.external.kakao.dto;


import java.util.List;

public record KakaoGeocodeResponse(
        Meta meta,
        List<Document> documents
) {
    public record Meta(
            int total_count
    ) {}

    public record Document(
            String address_name,
            String y,
            String x,
            String address_type,
            Address address
    ) {}

    public record Address(
            String region_1depth_name,
            String region_2depth_name,
            String region_3depth_name,
            String mountain_yn,
            String main_address_no,
            String sub_address_no,
            String zip_code
    ) {}
}


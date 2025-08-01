package com.localcoupon.couponservice.common.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoGeocodeResponse(
        List<Document> documents
) {
    public record Document(
            String address_name,
            String y,
            String x,
            String address_type,
            Address address,
            RoadAddress road_address
    ) {}

    public record Address(
            String address_name,
            String h_code,
            String b_code,
            String main_address_no,
            String sub_address_no,
            String x,
            String y,
            String zip_code
    ) {}

    public record RoadAddress(
            String address_name,
            String road_name,
            String building_name,
            String y,
            String x
    ) {}
}

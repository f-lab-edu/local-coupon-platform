package com.localcoupon.couponservice.common.external.kakao.dto;

import java.util.List;

public record KakaoGeocodeResponse(
        Meta meta,
        List<Document> documents
) {
    public record Meta(
            int total_count,
            int pageable_count,
            boolean is_end
    ) {}

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
            String region_1depth_name,
            String region_2depth_name,
            String region_3depth_name,
            String region_3depth_h_name,
            String h_code,
            String b_code,
            String mountain_yn,
            String main_address_no,
            String sub_address_no,
            String x,
            String y,
            String zip_code
    ) {}

    public record RoadAddress(
            String address_name,
            String region_1depth_name,
            String region_2depth_name,
            String region_3depth_name,
            String road_name,
            String underground_yn,
            String main_building_no,
            String sub_building_no,
            String building_name,
            String zone_no,
            String y,
            String x
    ) {}
}

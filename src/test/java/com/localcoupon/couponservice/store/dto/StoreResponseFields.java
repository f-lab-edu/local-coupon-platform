package com.localcoupon.couponservice.store.dto;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class StoreResponseFields {

    public static FieldDescriptor[] storeResponseFields(String prefix) {
        return new FieldDescriptor[]{
                fieldWithPath(prefix + "id").description("매장 ID").optional(),
                fieldWithPath(prefix + "name").description("매장 이름"),
                fieldWithPath(prefix + "address").description("매장 주소"),
                fieldWithPath(prefix + "category").description("매장 카테고리"),
                fieldWithPath(prefix + "latitude").description("매장 위도"),
                fieldWithPath(prefix + "longitude").description("매장 경도"),
                fieldWithPath(prefix + "phoneNumber").description("매장 전화번호"),
                fieldWithPath(prefix + "description").description("매장 설명"),
                fieldWithPath(prefix + "imageUrl").description("매장 이미지 URL"),
                fieldWithPath(prefix + "createdAt").description("매장 등록일자").optional()
        };
    }
}

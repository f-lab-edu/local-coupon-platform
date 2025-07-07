package com.localcoupon.couponservice.store.entity;

import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store {

    public Store(StoreRequestDto request,
                 KakaoGeocodeInfoDto geoCodeInfo,
                 Long ownerId,
                 String imageUrl) {
        this.ownerId = ownerId;
        this.name = request.name();
        this.address = request.address();
        this.category = request.category();
        this.regionCode = geoCodeInfo.regionCode();
        this.latitude = geoCodeInfo.latitude();
        this.longitude = geoCodeInfo.longitude();
        this.phoneNumber = request.phoneNumber();
        this.description = request.description();
        this.imageUrl = imageUrl;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private Long ownerId;

    @Column
    @NotNull
    @Size(max=255)
    private String name;

    @Column
    @NotNull
    @Size(max=255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column
    @Size(max=255)
    private StoreCategory category;

    @Column
    private String regionCode;

    @Column
    @Digits(integer = 2, fraction = 6)
    private BigDecimal latitude;

    @Column
    @Digits(integer = 3, fraction = 6)
    private BigDecimal longitude;

    @Column
    private String phoneNumber;

    @Column
    @Size(max=500)
    private String description;

    @Column
    @Size(max=500)
    private String imageUrl;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Boolean isDeleted;

    public static Store from(
            StoreRequestDto request,
            KakaoGeocodeInfoDto geocodeInfo,
            Long ownerId,
            String imageUrl
    ) {
        return new Store(request,geocodeInfo,ownerId,imageUrl);
    }
}


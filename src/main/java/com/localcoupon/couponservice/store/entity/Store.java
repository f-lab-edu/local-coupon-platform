package com.localcoupon.couponservice.store.entity;

import com.localcoupon.couponservice.common.entity.BaseEntity;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    public Store(StoreRequestDto request,
                 KakaoGeocodeInfoDto geoCodeInfo,
                 Long ownerId) {
        this.ownerId = ownerId;
        this.name = request.name();
        this.address = request.address();
        this.category = request.category();
        this.regionCode = geoCodeInfo.regionCode();
        this.latitude = geoCodeInfo.latitude();
        this.longitude = geoCodeInfo.longitude();
        this.phoneNumber = request.phoneNumber();
        this.description = request.description();
        this.imageUrl = request.imageUrl();
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

    @OneToMany(mappedBy = "store")
    private List<Coupon> coupons;


    public static Store from(
            StoreRequestDto request,
            KakaoGeocodeInfoDto geocodeInfo,
            Long ownerId
    ) {
        KakaoGeocodeInfoDto safeGeoCodeInfo = Optional.ofNullable(geocodeInfo)
                .orElse(KakaoGeocodeInfoDto.of("UNKNOWN", BigDecimal.ZERO, BigDecimal.ZERO));

        return new Store(request, safeGeoCodeInfo,ownerId);
    }
}


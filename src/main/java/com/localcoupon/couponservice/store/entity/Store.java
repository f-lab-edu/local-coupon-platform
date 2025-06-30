package com.localcoupon.couponservice.store.entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
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
}


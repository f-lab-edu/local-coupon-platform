package com.localcoupon.couponservice.store.repository;

import com.localcoupon.couponservice.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    @Query("""
        SELECT s
        FROM Store s
        WHERE s.isDeleted = false
          AND s.latitude BETWEEN :minLat AND :maxLat
          AND s.longitude BETWEEN :minLng AND :maxLng
    """)
    List<Store> findByLatLngRange(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng
    );
}


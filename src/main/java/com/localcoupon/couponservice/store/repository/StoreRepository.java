package com.localcoupon.couponservice.store.repository;

import com.localcoupon.couponservice.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwnerId(Long ownerId);
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


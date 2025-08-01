package com.localcoupon.couponservice.store.service.impl;

import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.external.kakao.KakaoGeocodeService;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import com.localcoupon.couponservice.store.service.StoreService;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final KakaoGeocodeService kakaoGeocodeService;

    @Override
    public StoreResponseDto registerStore(StoreRequestDto request, Long userId) {
        // Kakao 좌표 변환 호출
        KakaoGeocodeInfoDto geoCodeInfo = kakaoGeocodeService.geocode(request.address());

        log.info(geoCodeInfo.toString());

        Store store = Store.from(request, geoCodeInfo, userId); //TODO : 이미지 처리 추가 필요

        //Store 저장
        Store saved = storeRepository.save(store);

        return StoreResponseDto.fromEntity(saved);
    }

    @Override
    public List<StoreResponseDto> getMyStores(Long userId) {
        List<Store> stores = storeRepository.findByOwnerIdAndIsDeletedFalse(userId);

        return stores.stream()
                .map(StoreResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreResponseDto> getStoresNearby(UserStoreSearchRequestDto request) {
        if(request.minLatitude() == null || request.maxLatitude() == null ||
                request.minLongitude() == null || request.maxLongitude() == null) {
            throw new CommonException(CommonErrorCode.GEO_LOCATION_ERROR);
        }

        List<Store> stores = storeRepository.findByLatLngRange(
                request.minLatitude(),
                request.maxLatitude(),
                request.minLongitude(),
                request.maxLongitude()
        );

        return stores.stream()
                .map(StoreResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


}


package com.localcoupon.couponservice.store.service;

import com.localcoupon.couponservice.common.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.GeoException;
import com.localcoupon.couponservice.common.external.kakao.KakaoGeocodeService;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
import com.localcoupon.couponservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import com.localcoupon.couponservice.store.service.impl.StoreServiceImpl;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StoreServiceTest {

    private StoreRepository storeRepository;
    private UserRepository userRepository;
    private KakaoGeocodeService kakaoGeocodeService;
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        storeRepository = mock(StoreRepository.class);
        userRepository = mock(UserRepository.class);
        kakaoGeocodeService = mock(KakaoGeocodeService.class);
        storeService = new StoreServiceImpl(storeRepository, userRepository, kakaoGeocodeService);
    }

    @Test
    @DisplayName("registerStore - 유저 존재 시 매장 등록 성공")
    void registerStore_success() {
        // given
        String email = "test@example.com";
        Long userId = 10L;

        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        KakaoGeocodeInfoDto geoCodeInfo = new KakaoGeocodeInfoDto(
                "110105",
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276")
        );

        Store store = Store.builder()
                .id(1L)
                .ownerId(userId)
                .name("스타벅스")
                .address("서울시 송파구 법원로 55")
                .category(StoreCategory.CAFE)
                .phoneNumber("02-1234-5678")
                .description("매장 설명입니다.")
                .imageUrl("http://example.com/image.jpg")
                .latitude(new BigDecimal("37.4979"))
                .longitude(new BigDecimal("127.0276"))
                .build();

        given(userRepository.findIdByEmail(email)).willReturn(Optional.of(userId));
        given(kakaoGeocodeService.geocode(anyString())).willReturn(geoCodeInfo);
        given(storeRepository.save(any(Store.class))).willReturn(store);

        // when
        StoreResponseDto result = storeService.registerStore(request, email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("스타벅스");
        assertThat(result.address()).isEqualTo("서울시 송파구 법원로 55");
        assertThat(result.phoneNumber()).isEqualTo("02-1234-5678");
        assertThat(result.description()).isEqualTo("매장 설명입니다.");
        assertThat(result.imageUrl()).isEqualTo("http://example.com/image.jpg");
    }

    @Test
    @DisplayName("registerStore - 유저가 존재하지 않으면 UserNotFoundException 발생")
    void registerStore_userNotFound() {
        // given
        String email = "notfound@example.com";

        StoreRequestDto request = new StoreRequestDto(
                "스타벅스",
                "서울시 송파구 법원로 55",
                StoreCategory.CAFE,
                "02-1234-5678",
                "매장 설명입니다.",
                "http://example.com/image.jpg"
        );

        given(userRepository.findIdByEmail(email)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> storeService.registerStore(request, email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getMyStores - 유저가 가진 매장 목록 반환")
    void getMyStores_success() {
        // given
        String email = "test@example.com";
        Long userId = 1L;

        Store store = Store.builder()
                .id(1L)
                .ownerId(userId)
                .name("매장이름")
                .address("서울시 송파구 법원로 55")
                .latitude(new BigDecimal("37.4979"))
                .longitude(new BigDecimal("127.0276"))
                .phoneNumber("02-1234-5678")
                .description("커피 전문점")
                .imageUrl("http://example.com/image.jpg")
                .build();

        given(userRepository.findIdByEmail(email)).willReturn(Optional.of(userId));
        given(storeRepository.findByOwnerIdAndIsDeletedFalse(anyLong()))
                .willReturn(List.of(store));

        // when
        List<StoreResponseDto> result = storeService.getMyStores(email);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("매장이름");
    }

    @Test
    @DisplayName("getStoresNearby - 정상 좌표 요청 시 매장 목록 반환")
    void getStoresNearby_success() {
        // given
        UserStoreSearchRequestDto request = new UserStoreSearchRequestDto(
                new BigDecimal("37.48"),
                new BigDecimal("37.50"),
                new BigDecimal("126.98"),
                new BigDecimal("127.02")
        );

        Store store = Store.builder()
                .id(1L)
                .ownerId(1L)
                .name("매장이름")
                .address("서울시 송파구 법원로 55")
                .latitude(new BigDecimal("37.4979"))
                .longitude(new BigDecimal("127.0276"))
                .phoneNumber("02-1234-5678")
                .description("커피 전문점")
                .imageUrl("http://example.com/image.jpg")
                .build();

        given(storeRepository.findByLatLngRange(any(), any(), any(), any()))
                .willReturn(List.of(store));

        // when
        List<StoreResponseDto> result = storeService.getStoresNearby(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("매장이름");
    }

    @Test
    @DisplayName("getStoresNearby - 좌표 null이면 GeoException 발생")
    void getStoresNearby_geoException() {
        // given
        UserStoreSearchRequestDto request = new UserStoreSearchRequestDto(
                null, null, null, null
        );

        // when / then
        assertThatThrownBy(() -> storeService.getStoresNearby(request))
                .isInstanceOf(GeoException.class)
                .hasMessageContaining(CommonErrorCode.GEO_LOCATION_ERROR.getMessage());
    }
}

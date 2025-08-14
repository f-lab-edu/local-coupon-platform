package com.localcoupon.otherservice.store.service;

import static com.localcoupon.otherservice.fixtures.KakaoGeocodeFixture.geo;
import static com.localcoupon.otherservice.fixtures.StoreFixture.store;
import static com.localcoupon.otherservice.fixtures.StoreRequestFixture.request;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.localcoupon.otherservice.common.exception.CommonErrorCode;
import com.localcoupon.otherservice.common.exception.CommonException;
import com.localcoupon.otherservice.common.external.kakao.KakaoGeocodeService;
import com.localcoupon.otherservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.dto.request.UserStoreSearchRequestDto;
import com.localcoupon.otherservice.store.dto.response.StoreResponseDto;
import com.localcoupon.otherservice.store.entity.Store;
import com.localcoupon.otherservice.store.repository.StoreRepository;
import com.localcoupon.otherservice.store.service.impl.StoreServiceImpl;
import com.localcoupon.otherservice.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    storeService = new StoreServiceImpl(storeRepository, kakaoGeocodeService);
  }

  @Test
  @DisplayName("registerStore - 유저 존재 시 매장 등록 성공")
  void registerStore_success() {
    // Given
    Long userId = 10L;
    StoreRequestDto requestDto = request();                 // Fixture 사용
    KakaoGeocodeInfoDto geoCodeInfo = geo();                // Fixture 사용
    Store saved = store(b -> b.ownerId(userId));            // 일부만 오버라이드

    given(userRepository.findIdByEmail(anyString())).willReturn(Optional.of(userId));
    given(kakaoGeocodeService.geocode(anyString())).willReturn(geoCodeInfo);
    given(storeRepository.save(any(Store.class))).willReturn(saved);

    // When
    StoreResponseDto result = storeService.registerStore(requestDto, userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("스타벅스");
    assertThat(result.address()).isEqualTo("서울시 송파구 법원로 55");
    assertThat(result.phoneNumber()).isEqualTo("02-1234-5678");
    assertThat(result.description()).isEqualTo("매장 설명입니다.");
    assertThat(result.imageUrl()).isEqualTo("http://example.com/image.jpg");
  }

  @Test
  @DisplayName("getMyStores - 유저가 가진 매장 목록 반환")
  void getMyStores_success() {
    // Given
    Long userId = 1L;
    Store s1 = store(b -> b
        .id(1L)
        .ownerId(userId)
        .name("매장이름")
        .address("서울시 송파구 법원로 55"));

    given(userRepository.findIdByEmail(anyString())).willReturn(Optional.of(userId));
    given(storeRepository.findFirstByOwnerId(anyLong()))
        .willReturn(Optional.of(s1));

    // When
    StoreResponseDto result = storeService.getMyStores(userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("매장이름");
    assertThat(result.address()).isEqualTo("서울시 송파구 법원로 55");
  }

  @Test
  @DisplayName("getStoresNearby - 정상 좌표 요청 시 매장 목록 반환")
  void getStoresNearby_success() {
    // Given
    UserStoreSearchRequestDto req = new UserStoreSearchRequestDto(
        new BigDecimal("37.48"),
        new BigDecimal("37.50"),
        new BigDecimal("126.98"),
        new BigDecimal("127.02")
    );

    Store s1 = store(b -> b
        .id(1L)
        .ownerId(1L)
        .name("매장이름")
        .address("서울시 송파구 법원로 55"));

    given(storeRepository.findByLatLngRange(any(), any(), any(), any()))
        .willReturn(List.of(s1));

    // When
    List<StoreResponseDto> result = storeService.getStoresNearby(req);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("매장이름");
  }

  @Test
  @DisplayName("getStoresNearby - 좌표 null이면 GeoException 발생")
  void getStoresNearby_geoException() {
    // Given
    UserStoreSearchRequestDto req = new UserStoreSearchRequestDto(null, null, null, null);

    // When / Then
    assertThatThrownBy(() -> storeService.getStoresNearby(req))
        .isInstanceOf(CommonException.class)
        .hasMessageContaining(CommonErrorCode.GEO_LOCATION_ERROR.getMessage());
  }
}

package com.my.pharmacy.service;

import com.my.pharmacy.dto.KakaoApiResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KakaoAddressSearchServiceTest {

    @Autowired
    KakaoAddressSearchService kakaoAddressSearchService;

    @Autowired
    KakaoCategorySearchService kakaoCategorySearchService;

    @Test
    @DisplayName("주소 검색 → 위도/경도 변환 테스트")
    void getCoordinatesTest() {
        double[] coords = kakaoAddressSearchService.getCoordinates("강남대로 405");

        // 위도, 경도 값이 반환되는지 검증
        assertNotNull(coords);
        assertEquals(2, coords.length);
        System.out.println("위도: " + coords[0] + ", 경도: " + coords[1]);
    }

    @Test
    @DisplayName("카테고리 검색 테스트 (약국)")
    void categoryTest() {
        double lat = 37.49855885145178;   // 강남역 근처 위도
        double lng = 127.0263154489116;   // 강남역 근처 경도
        double radius = 1000; // 1km 반경

        KakaoApiResponseDto dto = kakaoCategorySearchService
                .resultCategorySearch(lat, lng, radius);

        assertNotNull(dto);
        assertNotNull(dto.getDocumentList());
        System.out.println("검색 결과 수: " + dto.getDocumentList().size());
    }
}

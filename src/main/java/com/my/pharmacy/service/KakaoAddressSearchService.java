package com.my.pharmacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    @Value("${KAKAO_REST_API_KEY}") // application.yml 또는 환경변수에서 가져옴
    private String kakaoRestApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 입력받은 주소를 카카오 API로 좌표 변환
     * @param address 사용자가 입력한 주소
     * @return double[]{latitude, longitude}
     */
    public double[] getCoordinates(String address) {
        try {
            String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

            // 헤더에 인증키 추가
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoRestApiKey);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<String> response =
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode documents = root.path("documents");
            if (documents.isArray() && documents.size() > 0) {
                JsonNode first = documents.get(0);
                double longitude = first.path("x").asDouble();
                double latitude = first.path("y").asDouble();
                return new double[]{latitude, longitude};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 실패 시 기본 좌표 (서울 시청)
        return new double[]{37.5665, 126.9780};
    }
}

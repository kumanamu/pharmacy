package com.my.pharmacy.controller;

import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.service.KakaoAddressSearchService;
import com.my.pharmacy.service.KakaoCategorySearchService;
import com.my.pharmacy.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final PharmacyService pharmacyService;

    // POST 요청 처리 (form 제출 시)
    @PostMapping("/search")
    public String searchPharmacyPost(@RequestParam("address") String address, Model model) {
        return handleSearch(address, model);
    }

    // GET 요청 처리 (브라우저 직접 호출 시)
    @GetMapping("/search")
    public String searchPharmacyGet(@RequestParam("address") String address, Model model) {
        return handleSearch(address, model);
    }

    // 실제 공통 로직
    private String handleSearch(String address, Model model) {

        if (address == null || address.trim().isEmpty()) {
            model.addAttribute("errorMessage", "주소를 입력하세요.");
            return "main";
        }

        double[] coords = kakaoAddressSearchService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        var response = kakaoCategorySearchService.resultCategorySearch(latitude, longitude, 1000);
        var result = kakaoCategorySearchService.makeOutputDto(response.getDocumentList());

        // ✅ 로그 추가
        System.out.println("=== 검색 요청 주소: " + address);
        System.out.println("=== 검색 결과 건수: " + result.size());

        // DB 저장
        result.forEach(dto -> {
            System.out.println("저장 시도 → " + dto); // ✅ 저장 시도 로그

            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setName(dto.getPharmacyName());
            pharmacy.setLatitude(dto.getLatitude());
            pharmacy.setLongitude(dto.getLongitude());
            try {
                pharmacy.setDistance(Double.parseDouble(dto.getDistance()));
            } catch (NumberFormatException e) {
                pharmacy.setDistance(0.0);
            }

            pharmacyService.savePharmacy(pharmacy);
            System.out.println("저장 완료 → " + pharmacy.getName()); // ✅ 저장 완료 로그
        });

        model.addAttribute("outputList", result);
        return "output";
    }
}

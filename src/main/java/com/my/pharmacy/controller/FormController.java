package com.my.pharmacy.controller;

import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.service.KakaoAddressSearchService;
import com.my.pharmacy.service.KakaoCategorySearchService;
import com.my.pharmacy.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
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
        log.info("📍 Address received = {}", address);

        double[] coords = kakaoAddressSearchService.getCoordinates(address);
        log.info("📍 Coordinates = lat={}, lon={}", coords[0], coords[1]);

        var response = kakaoCategorySearchService.resultCategorySearch(latitude, longitude, 1000);
        log.info("📦 Kakao API returned {} docs", response.getDocumentList().size());

        var result = kakaoCategorySearchService.makeOutputDto(response.getDocumentList());
        log.info("📋 Processed {} pharmacies", result.size());

        result.forEach(dto -> {
            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setName(dto.getPharmacyName());
            pharmacy.setLatitude(dto.getLatitude());
            pharmacy.setLongitude(dto.getLongitude());
            try {
                pharmacy.setDistance(Double.parseDouble(dto.getDistance()));
            } catch (NumberFormatException e) {
                pharmacy.setDistance(0.0);
            }
            log.info("➡️ Ready to save: {}", pharmacy.getName());
            pharmacyService.savePharmacy(pharmacy);
        });

        model.addAttribute("outputList", result);
        return "output";
    }
}

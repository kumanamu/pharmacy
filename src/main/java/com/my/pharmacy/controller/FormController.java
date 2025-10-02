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

    @PostMapping("/search")
    public String searchPharmacyPost(@RequestParam(value = "address", required = false) String address, Model model) {
        if (address == null || address.isBlank()) {
            model.addAttribute("error", "주소를 입력해주세요.");
            return "search"; // 다시 입력 페이지
        }
        return handleSearch(address, model);
    }

    @GetMapping("/search")
    public String searchPharmacyGet(@RequestParam(value = "address", required = false) String address, Model model) {
        if (address == null || address.isBlank()) {
            model.addAttribute("error", "주소를 입력해주세요.");
            return "search";
        }
        return handleSearch(address, model);
    }

    private String handleSearch(String address, Model model) {
        log.info("📍 Address received = {}", address);

        double[] coords = kakaoAddressSearchService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];
        log.info("📍 Coordinates = lat={}, lon={}", latitude, longitude);

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
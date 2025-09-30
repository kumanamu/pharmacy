package com.my.pharmacy.controller;

import com.my.pharmacy.dto.OutputDto;
import com.my.pharmacy.dto.KakaoApiResponseDto;
import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.service.KakaoAddressSearchService;
import com.my.pharmacy.service.KakaoCategorySearchService;
import com.my.pharmacy.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final PharmacyService pharmacyService;

    @PostMapping("/search")
    public String searchPharmacy(@RequestParam("address") String address, Model model) {
        if (address == null || address.trim().isEmpty()) {
            // 주소가 비어있으면 그냥 메인 페이지로 다시 돌려보냄
            model.addAttribute("errorMessage", "주소를 입력하세요.");
            return "main"; // 다시 main.html 보여줌
        }

        double[] coords = kakaoAddressSearchService.getCoordinates(address);
        double latitude = coords[0];
        double longitude = coords[1];

        KakaoApiResponseDto response = kakaoCategorySearchService.resultCategorySearch(latitude, longitude, 1000);
        List<OutputDto> result = kakaoCategorySearchService.makeOutputDto(response.getDocumentList());

        model.addAttribute("outputList", result);
        return "output";
    }
}
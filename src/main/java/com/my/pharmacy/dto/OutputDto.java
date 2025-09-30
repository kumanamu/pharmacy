package com.my.pharmacy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class OutputDto {
    private String pharmacyName;
    private String pharmacyAddress;
    private String directionURL;
    private String roadViewURL;
    private String distance;

    private double latitude;   // ✅ 추가
    private double longitude;  // ✅ 추가
}

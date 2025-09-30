package com.my.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pharmacy")
@Getter
@Setter
@NoArgsConstructor
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;   // 약국명

    @Column(nullable = false)
    private double latitude;   // 위도

    @Column(nullable = false)
    private double longitude;  // 경도

    @Column(nullable = false)
    private double distance;   // 거리
}

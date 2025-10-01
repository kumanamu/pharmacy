package com.my.pharmacy.service;

import com.my.pharmacy.entity.Pharmacy;
import com.my.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    @Transactional
    public Pharmacy savePharmacy(Pharmacy pharmacy) {
        log.info("💾 Saving pharmacy: {}", pharmacy);
        Pharmacy saved = pharmacyRepository.save(pharmacy);
        log.info("✅ Saved pharmacy with id={}", saved.getId());
        return saved;
    }


    @Transactional(readOnly = true)
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }
}

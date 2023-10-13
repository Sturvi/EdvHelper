package com.example.edvhelper.service;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.repository.FiscalIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FiscalIdService {
    private final FiscalIdRepository fiscalIdRepository;



    public void save(FiscalId fiscalId) {
        fiscalIdRepository.save(fiscalId);
    }

    public Optional<FiscalId> getByStatus(FiscalIdStatusEnum fiscalIdStatusEnum) {
        return fiscalIdRepository.findFirstByStatus(fiscalIdStatusEnum);
    }

    public List<FiscalId> getAvailableFiscalId(AppUser appUser) {
        return fiscalIdRepository.findByAppUserAndStatusAndChequeDateBefore(appUser,
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(30));
    }

    public List<FiscalId> getAllAvailableFiscalId() {
        return fiscalIdRepository.findByStatusAndChequeDateBefore(
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(30));
    }

    public List<FiscalId> getExpiringFiscalId() {
        return fiscalIdRepository.findByStatusAndChequeDateBefore(
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(80));
    }

    public List<FiscalId> getWaitingList(AppUser appUser) {
        return fiscalIdRepository.findByAppUserAndStatusAndChequeDateAfter(
                appUser,
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(30));
    }
}

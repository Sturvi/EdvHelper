package com.example.edvhelper.service;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.repository.FiscalIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<FiscalId> getByStatus(FiscalIdStatusEnum fiscalIdStatusEnum) {
        return fiscalIdRepository.findByStatus(fiscalIdStatusEnum);
    }

    public List<FiscalId> getAvailableFiscalId(AppUser appUser) {
        return fiscalIdRepository.findByAppUserAndStatusAndChequeDateBefore(appUser,
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(29));
    }

    public List<FiscalId> getByAppUserStatusAndDate (AppUser appUser, FiscalIdStatusEnum statusEnum, LocalDate date){
        return fiscalIdRepository.findByAppUserAndStatusAndChequeDateBefore(appUser, statusEnum, date);
    }

    public List<FiscalId> getAllAvailableFiscalId() {
        return fiscalIdRepository.findByStatusAndChequeDateBefore(
                FiscalIdStatusEnum.UNUSED,
                LocalDate.now().minusDays(29));
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

    public Long getAvaibleSecretCount (AppUser appUser) {
        return fiscalIdRepository.countFiscalIdsByDateRangeAndStatus(appUser, FiscalIdStatusEnum.FROM_API_UNUSED, LocalDate.now().minusDays(90), LocalDate.now().minusDays(87));
    }

    public BigDecimal getAvaibleSecretSum (AppUser appUser) {
        return fiscalIdRepository.sumReturnAmountsByDateRangeAndStatus(appUser, FiscalIdStatusEnum.FROM_API_UNUSED, LocalDate.now().minusDays(90), LocalDate.now().minusDays(87));
    }

    public Optional<FiscalId> getFiscalIdWithMaxReturnAmount(AppUser appUser) {
        Pageable pageable = PageRequest.of(0, 1); // Создаем объект Pageable для запроса только одного элемента

        // Получаем список с максимум одним результатом
        List<FiscalId> fiscalIds = fiscalIdRepository.findTopByAppUserAndStatusWithMaxReturnAmountWithinDateRange(
                appUser.getId(),
                FiscalIdStatusEnum.FROM_API_UNUSED,
                LocalDate.now().minusDays(90),
                LocalDate.now().minusDays(87),
                pageable);

        // Возвращаем первый элемент, если он есть
        return fiscalIds.isEmpty() ? Optional.empty() : Optional.of(fiscalIds.get(0));
    }

    public Optional<FiscalId> getFiscalId (String fiscalId) {
        return fiscalIdRepository.findFirstByFiscalId(fiscalId);
    }
}

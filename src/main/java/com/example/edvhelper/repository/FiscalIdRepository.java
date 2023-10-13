package com.example.edvhelper.repository;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FiscalIdRepository extends JpaRepository<FiscalId, Long> {

    Optional<FiscalId> findFirstByStatus(FiscalIdStatusEnum status);

    List<FiscalId> findByAppUserAndStatusAndChequeDateBefore(AppUser appUser, FiscalIdStatusEnum status, LocalDate date);

    List<FiscalId> findByStatusAndChequeDateBefore(FiscalIdStatusEnum status, LocalDate date);

    List<FiscalId> findByAppUserAndStatusAndChequeDateAfter(AppUser appUser, FiscalIdStatusEnum status, LocalDate date);
}

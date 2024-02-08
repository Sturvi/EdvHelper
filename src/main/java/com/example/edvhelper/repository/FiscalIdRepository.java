package com.example.edvhelper.repository;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FiscalIdRepository extends JpaRepository<FiscalId, Long> {

    Optional<FiscalId> findFirstByStatus(FiscalIdStatusEnum status);

    List<FiscalId> findByStatus(FiscalIdStatusEnum status);
    Optional<FiscalId> findFirstByFiscalId(String fiscalId);

    List<FiscalId> findByAppUserAndStatusAndChequeDateBefore(AppUser appUser, FiscalIdStatusEnum status, LocalDate date);

    List<FiscalId> findByStatusAndChequeDateBefore(FiscalIdStatusEnum status, LocalDate date);

    List<FiscalId> findByAppUserAndStatusAndChequeDateAfter(AppUser appUser, FiscalIdStatusEnum status, LocalDate date);

    @Query("SELECT f FROM FiscalId f WHERE f.appUser.id = :appUserId AND f.status = :status " +
            "AND f.chequeDate BETWEEN :startDate AND :endDate " +
            "ORDER BY f.returnAmount DESC")
    List<FiscalId> findTopByAppUserAndStatusWithMaxReturnAmountWithinDateRange(
            @Param("appUserId") Long appUserId,
            @Param("status") FiscalIdStatusEnum status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    @Query("SELECT COUNT(f) FROM FiscalId f WHERE f.appUser = :appUser AND f.status = :status " +
            "AND f.chequeDate BETWEEN :startDate AND :endDate")
    Long countFiscalIdsByDateRangeAndStatus(
            @Param("appUser") AppUser appUser,
            @Param("status") FiscalIdStatusEnum status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT SUM(f.returnAmount) FROM FiscalId f WHERE f.appUser = :appUser AND f.status = :status " +
            "AND f.chequeDate BETWEEN :startDate AND :endDate")
    BigDecimal sumReturnAmountsByDateRangeAndStatus(
            @Param("appUser") AppUser appUser,
            @Param("status") FiscalIdStatusEnum status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}

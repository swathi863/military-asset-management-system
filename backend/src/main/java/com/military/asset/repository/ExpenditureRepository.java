package com.military.asset.repository;

import com.military.asset.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query("SELECT e FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate) " +
           "ORDER BY e.expenditureDate DESC")
    List<Expenditure> filterExpenditures(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate)")
    Integer sumExpendedQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipmentType.id = :equipmentTypeId) AND " +
           "e.expenditureDate < :startDate")
    Integer sumExpendedQuantityBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate
    );
}

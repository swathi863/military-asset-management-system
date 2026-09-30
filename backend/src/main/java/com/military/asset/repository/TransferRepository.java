package com.military.asset.repository;

import com.military.asset.entity.Transfer;
import com.military.asset.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT t FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.sourceBase.id = :baseId OR t.targetBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) " +
           "ORDER BY t.transferDate DESC")
    List<Transfer> filterTransfers(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Filter Transfers In by targetBase and completionDate in period.
     */
    @Query("SELECT t FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.targetBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.completionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.completionDate <= :endDate) AND " +
           "t.status = :status " +
           "ORDER BY t.completionDate DESC")
    List<Transfer> filterTransfersIn(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") TransferStatus status
    );

    /**
     * Filter Transfers Out by sourceBase and initiation transferDate in period (excluding CANCELLED).
     */
    @Query("SELECT t FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.sourceBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) AND " +
           "t.status != 'CANCELLED' " +
           "ORDER BY t.transferDate DESC")
    List<Transfer> filterTransfersOut(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Transfer In quantity sum for completed transfers received at targetBase on completionDate.
     */
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.targetBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.completionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.completionDate <= :endDate) AND " +
           "t.status = :status")
    Integer sumTransfersIn(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") TransferStatus status
    );

    /**
     * Transfer Out quantity sum for dispatched transfers from sourceBase on transferDate (excluding CANCELLED).
     */
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.sourceBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) AND " +
           "t.status != 'CANCELLED'")
    Integer sumTransfersOut(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Historical Transfer In before start date based on completion date.
     */
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.targetBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "t.completionDate < :startDate AND " +
           "t.status = :status")
    Integer sumTransfersInBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("status") TransferStatus status
    );

    /**
     * Historical Transfer Out before start date based on initiation date (excluding CANCELLED).
     */
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.sourceBase.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
           "t.transferDate < :startDate AND " +
           "t.status != 'CANCELLED'")
    Integer sumTransfersOutBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDateTime startDate
    );
}

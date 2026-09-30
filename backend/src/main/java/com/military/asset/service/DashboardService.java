package com.military.asset.service;

import com.military.asset.dto.DashboardMetricsDto;
import com.military.asset.dto.NetMovementDetailDto;
import com.military.asset.entity.AssignmentStatus;
import com.military.asset.entity.BaseInventory;
import com.military.asset.entity.Purchase;
import com.military.asset.entity.Transfer;
import com.military.asset.entity.TransferStatus;
import com.military.asset.repository.*;
import com.military.asset.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BaseInventoryRepository baseInventoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;

    public DashboardService(BaseInventoryRepository baseInventoryRepository,
                            PurchaseRepository purchaseRepository,
                            TransferRepository transferRepository,
                            AssignmentRepository assignmentRepository,
                            ExpenditureRepository expenditureRepository) {
        this.baseInventoryRepository = baseInventoryRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
    }

    public DashboardMetricsDto getDashboardMetrics(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        // Enforce RBAC Base Scope (Base Commander is locked to their assigned base)
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // Calculate exact Opening Balance for the period
        int openingBalanceForPeriod = calculateOpeningBalanceForPeriod(baseId, equipmentTypeId, startDateTime);

        // Purchases within period (by purchaseDate)
        Integer purchases = purchaseRepository.sumQuantityByFilter(baseId, equipmentTypeId, startDateTime, endDateTime);
        int totalPurchases = purchases != null ? purchases : 0;

        // Transfers In within period (by completionDate for completed transfers)
        Integer transfersIn = transferRepository.sumTransfersIn(baseId, equipmentTypeId, startDateTime, endDateTime, TransferStatus.COMPLETED);
        int totalTransfersIn = transfersIn != null ? transfersIn : 0;

        // Transfers Out within period (by transferDate for dispatched transfers, excluding CANCELLED)
        Integer transfersOut = transferRepository.sumTransfersOut(baseId, equipmentTypeId, startDateTime, endDateTime);
        int totalTransfersOut = transfersOut != null ? transfersOut : 0;

        // Assigned Assets within period
        Integer assigned = assignmentRepository.sumAssignedQuantity(baseId, equipmentTypeId, startDateTime, endDateTime, AssignmentStatus.ACTIVE);
        int totalAssigned = assigned != null ? assigned : 0;

        // Expended Assets within period
        Integer expended = expenditureRepository.sumExpendedQuantity(baseId, equipmentTypeId, startDateTime, endDateTime);
        int totalExpended = expended != null ? expended : 0;

        return new DashboardMetricsDto(
                openingBalanceForPeriod,
                totalPurchases,
                totalTransfersIn,
                totalTransfersOut,
                totalAssigned,
                totalExpended
        );
    }

    public NetMovementDetailDto getNetMovementDetails(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        // Enforce RBAC Base Scope
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        List<Purchase> purchases = purchaseRepository.filterPurchases(baseId, equipmentTypeId, startDateTime, endDateTime);
        List<Transfer> transfersIn = transferRepository.filterTransfersIn(baseId, equipmentTypeId, startDateTime, endDateTime, TransferStatus.COMPLETED);
        List<Transfer> transfersOut = transferRepository.filterTransfersOut(baseId, equipmentTypeId, startDateTime, endDateTime);

        int purchasesQty = purchases.stream().mapToInt(Purchase::getQuantity).sum();
        int transfersInQty = transfersIn.stream().mapToInt(Transfer::getQuantity).sum();
        int transfersOutQty = transfersOut.stream().mapToInt(Transfer::getQuantity).sum();

        return new NetMovementDetailDto(
                purchasesQty,
                transfersInQty,
                transfersOutQty,
                purchases,
                transfersIn,
                transfersOut
        );
    }

    /**
     * Calculates stock at beginning of reporting period (T_start).
     * Opening Balance(T_start) = Initial Stock + Purchases(<T_start) + TransfersIn(<T_start) - TransfersOut(<T_start) - Expended(<T_start)
     */
    private int calculateOpeningBalanceForPeriod(Long baseId, Long equipmentTypeId, LocalDateTime startDateTime) {
        List<BaseInventory> inventories;
        if (baseId != null && equipmentTypeId != null) {
            inventories = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(baseId, equipmentTypeId)
                    .map(List::of).orElse(List.of());
        } else if (baseId != null) {
            inventories = baseInventoryRepository.findByBaseId(baseId);
        } else if (equipmentTypeId != null) {
            inventories = baseInventoryRepository.findByEquipmentTypeId(equipmentTypeId);
        } else {
            inventories = baseInventoryRepository.findAll();
        }

        int initialStock = inventories.stream().mapToInt(BaseInventory::getOpeningBalance).sum();

        if (startDateTime == null) {
            return initialStock;
        }

        // Transactions prior to startDateTime
        Integer prevPurchases = purchaseRepository.sumQuantityBeforeDate(baseId, equipmentTypeId, startDateTime);
        Integer prevTransfersIn = transferRepository.sumTransfersInBeforeDate(baseId, equipmentTypeId, startDateTime, TransferStatus.COMPLETED);
        Integer prevTransfersOut = transferRepository.sumTransfersOutBeforeDate(baseId, equipmentTypeId, startDateTime);
        Integer prevExpended = expenditureRepository.sumExpendedQuantityBeforeDate(baseId, equipmentTypeId, startDateTime);

        int pIn = prevPurchases != null ? prevPurchases : 0;
        int tIn = prevTransfersIn != null ? prevTransfersIn : 0;
        int tOut = prevTransfersOut != null ? prevTransfersOut : 0;
        int exp = prevExpended != null ? prevExpended : 0;

        return Math.max(0, initialStock + pIn + tIn - tOut - exp);
    }
}

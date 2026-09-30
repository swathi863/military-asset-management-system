package com.military.asset.service;

import com.military.asset.dto.ExpenditureRequestDto;
import com.military.asset.entity.Base;
import com.military.asset.entity.BaseInventory;
import com.military.asset.entity.EquipmentType;
import com.military.asset.entity.Expenditure;
import com.military.asset.repository.BaseInventoryRepository;
import com.military.asset.repository.BaseRepository;
import com.military.asset.repository.EquipmentTypeRepository;
import com.military.asset.repository.ExpenditureRepository;
import com.military.asset.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final BaseInventoryRepository baseInventoryRepository;
    private final AuditLogService auditLogService;

    public ExpenditureService(ExpenditureRepository expenditureRepository,
                               BaseRepository baseRepository,
                               EquipmentTypeRepository equipmentTypeRepository,
                               BaseInventoryRepository baseInventoryRepository,
                               AuditLogService auditLogService) {
        this.expenditureRepository = expenditureRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.baseInventoryRepository = baseInventoryRepository;
        this.auditLogService = auditLogService;
    }

    public Expenditure recordExpenditure(ExpenditureRequestDto request) {
        try {
            if (request.getBaseId() == null) {
                throw new IllegalArgumentException("Base is required.");
            }
            if (request.getEquipmentTypeId() == null) {
                throw new IllegalArgumentException("Equipment Type is required.");
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Expenditure quantity must be greater than 0.");
            }
            if (!StringUtils.hasText(request.getReason())) {
                throw new IllegalArgumentException("Expenditure reason is required.");
            }

            // RBAC Check
            SecurityUtils.checkBaseWritePermission(request.getBaseId());

            Base base = baseRepository.findById(request.getBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getBaseId()));

            EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment Type not found: " + request.getEquipmentTypeId()));

            BaseInventory inventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(base.getId(), equipmentType.getId())
                    .orElseThrow(() -> new IllegalStateException("No inventory record for asset at base"));

            int unassignedAvailable = inventory.getCurrentStock() - inventory.getAssignedCount();
            if (unassignedAvailable < request.getQuantity()) {
                throw new IllegalStateException("Cannot expend more than available unassigned stock (" + unassignedAvailable + "). Requested: " + request.getQuantity());
            }

            Expenditure expenditure = new Expenditure();
            expenditure.setExpenditureCode("EXP-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            expenditure.setBase(base);
            expenditure.setEquipmentType(equipmentType);
            expenditure.setQuantity(request.getQuantity());
            expenditure.setReason(request.getReason());
            expenditure.setOperationName(request.getOperationName());
            expenditure.setRemarks(request.getRemarks());
            expenditure.setExpenditureDate(LocalDateTime.now());

            // Deduct from current stock and add to expended count
            inventory.setCurrentStock(inventory.getCurrentStock() - request.getQuantity());
            inventory.setExpendedCount(inventory.getExpendedCount() + request.getQuantity());
            baseInventoryRepository.save(inventory);

            Expenditure savedExpenditure = expenditureRepository.save(expenditure);

            auditLogService.logAction(
                    "RECORD_EXPENDITURE",
                    "/api/expenditures",
                    "Expended " + request.getQuantity() + " " + equipmentType.getName() + " at " + base.getName() + " for " + request.getReason(),
                    base.getId(),
                    201
            );

            return savedExpenditure;
        } catch (Exception ex) {
            auditLogService.logAction(
                    "RECORD_EXPENDITURE_FAILED",
                    "/api/expenditures",
                    "Failed expenditure record attempt: " + ex.getMessage(),
                    request != null ? request.getBaseId() : null,
                    ex instanceof AccessDeniedException ? 403 : 400
            );
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<Expenditure> getExpenditures(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return expenditureRepository.filterExpenditures(baseId, equipmentTypeId, startDateTime, endDateTime);
    }
}

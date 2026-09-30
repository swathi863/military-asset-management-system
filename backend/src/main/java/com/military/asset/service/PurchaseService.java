package com.military.asset.service;

import com.military.asset.dto.PurchaseRequestDto;
import com.military.asset.entity.Base;
import com.military.asset.entity.BaseInventory;
import com.military.asset.entity.EquipmentType;
import com.military.asset.entity.Purchase;
import com.military.asset.repository.BaseInventoryRepository;
import com.military.asset.repository.BaseRepository;
import com.military.asset.repository.EquipmentTypeRepository;
import com.military.asset.repository.PurchaseRepository;
import com.military.asset.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final BaseInventoryRepository baseInventoryRepository;
    private final AuditLogService auditLogService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           BaseRepository baseRepository,
                           EquipmentTypeRepository equipmentTypeRepository,
                           BaseInventoryRepository baseInventoryRepository,
                           AuditLogService auditLogService) {
        this.purchaseRepository = purchaseRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.baseInventoryRepository = baseInventoryRepository;
        this.auditLogService = auditLogService;
    }

    public Purchase recordPurchase(PurchaseRequestDto request) {
        try {
            // Validation
            if (request.getBaseId() == null) {
                throw new IllegalArgumentException("Base is required.");
            }
            if (request.getEquipmentTypeId() == null) {
                throw new IllegalArgumentException("Equipment Type is required.");
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0.");
            }
            if (request.getUnitCost() == null || request.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Unit Cost cannot be negative.");
            }
            if (!StringUtils.hasText(request.getVendorName())) {
                throw new IllegalArgumentException("Vendor / Supplier Name is required.");
            }

            // RBAC Enforcement: Base Commander can only record purchases for their assigned base
            SecurityUtils.checkBaseWritePermission(request.getBaseId());

            Base base = baseRepository.findById(request.getBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Base not found with ID: " + request.getBaseId()));

            EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment Type not found with ID: " + request.getEquipmentTypeId()));

            Purchase purchase = new Purchase();
            purchase.setPurchaseOrderNumber("PO-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            purchase.setBase(base);
            purchase.setEquipmentType(equipmentType);
            purchase.setQuantity(request.getQuantity());
            purchase.setUnitCost(request.getUnitCost());
            purchase.setVendorName(request.getVendorName());
            purchase.setRemarks(request.getRemarks());
            purchase.setPurchaseDate(LocalDateTime.now());

            Purchase savedPurchase = purchaseRepository.save(purchase);

            // Update Base Inventory current_stock atomically
            BaseInventory inventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(base.getId(), equipmentType.getId())
                    .orElseGet(() -> new BaseInventory(base, equipmentType, 0, 0));

            inventory.setCurrentStock(inventory.getCurrentStock() + request.getQuantity());
            baseInventoryRepository.save(inventory);

            // Audit Log
            auditLogService.logAction(
                    "CREATE_PURCHASE",
                    "/api/purchases",
                    "Recorded Purchase " + savedPurchase.getPurchaseOrderNumber() + " (" + request.getQuantity() + " " + equipmentType.getName() + " for " + base.getName() + ")",
                    base.getId(),
                    201
            );

            return savedPurchase;
        } catch (Exception ex) {
            auditLogService.logAction(
                    "CREATE_PURCHASE_FAILED",
                    "/api/purchases",
                    "Failed purchase record attempt: " + ex.getMessage(),
                    request != null ? request.getBaseId() : null,
                    ex instanceof AccessDeniedException ? 403 : 400
            );
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchases(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return purchaseRepository.filterPurchases(baseId, equipmentTypeId, startDateTime, endDateTime);
    }
}

package com.military.asset.service;

import com.military.asset.dto.TransferRequestDto;
import com.military.asset.entity.Base;
import com.military.asset.entity.BaseInventory;
import com.military.asset.entity.EquipmentType;
import com.military.asset.entity.Role;
import com.military.asset.entity.Transfer;
import com.military.asset.entity.TransferStatus;
import com.military.asset.repository.BaseInventoryRepository;
import com.military.asset.repository.BaseRepository;
import com.military.asset.repository.EquipmentTypeRepository;
import com.military.asset.repository.TransferRepository;
import com.military.asset.security.SecurityUtils;
import com.military.asset.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransferService {

    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final BaseInventoryRepository baseInventoryRepository;
    private final AuditLogService auditLogService;

    public TransferService(TransferRepository transferRepository,
                           BaseRepository baseRepository,
                           EquipmentTypeRepository equipmentTypeRepository,
                           BaseInventoryRepository baseInventoryRepository,
                           AuditLogService auditLogService) {
        this.transferRepository = transferRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.baseInventoryRepository = baseInventoryRepository;
        this.auditLogService = auditLogService;
    }

    public Transfer initiateTransfer(TransferRequestDto request) {
        try {
            if (request.getSourceBaseId() == null || request.getTargetBaseId() == null) {
                throw new IllegalArgumentException("Source base and target base are required.");
            }
            if (request.getSourceBaseId().equals(request.getTargetBaseId())) {
                throw new IllegalArgumentException("Source base and target base cannot be identical.");
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Transfer quantity must be greater than 0.");
            }

            // RBAC Check: Base Commander can only initiate transfers from their assigned base
            SecurityUtils.checkBaseWritePermission(request.getSourceBaseId());

            Base sourceBase = baseRepository.findById(request.getSourceBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Source Base not found: " + request.getSourceBaseId()));

            Base targetBase = baseRepository.findById(request.getTargetBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Target Base not found: " + request.getTargetBaseId()));

            EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment Type not found: " + request.getEquipmentTypeId()));

            // Check source available unassigned stock
            BaseInventory sourceInventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(sourceBase.getId(), equipmentType.getId())
                    .orElseThrow(() -> new IllegalStateException("No inventory record for asset at source base"));

            int availableUnassigned = sourceInventory.getCurrentStock() - sourceInventory.getAssignedCount();
            if (availableUnassigned < request.getQuantity()) {
                throw new IllegalStateException("Cannot transfer more than available unassigned stock. Available: " + availableUnassigned + ", Requested: " + request.getQuantity());
            }

            Transfer transfer = new Transfer();
            transfer.setTransferCode("TRF-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            transfer.setSourceBase(sourceBase);
            transfer.setTargetBase(targetBase);
            transfer.setEquipmentType(equipmentType);
            transfer.setQuantity(request.getQuantity());
            transfer.setStatus(TransferStatus.IN_TRANSIT);
            transfer.setTransferDate(LocalDateTime.now());
            transfer.setRemarks(request.getRemarks());

            // Deduct from source stock immediately upon IN_TRANSIT
            sourceInventory.setCurrentStock(sourceInventory.getCurrentStock() - request.getQuantity());
            baseInventoryRepository.save(sourceInventory);

            Transfer savedTransfer = transferRepository.save(transfer);

            auditLogService.logAction(
                    "INITIATE_TRANSFER",
                    "/api/transfers",
                    "Initiated transfer " + savedTransfer.getTransferCode() + ": " + request.getQuantity() + " " + equipmentType.getName() + " from " + sourceBase.getName() + " to " + targetBase.getName(),
                    sourceBase.getId(),
                    201
            );

            return savedTransfer;
        } catch (Exception ex) {
            auditLogService.logAction(
                    "INITIATE_TRANSFER_FAILED",
                    "/api/transfers",
                    "Failed transfer initiation attempt: " + ex.getMessage(),
                    request != null ? request.getSourceBaseId() : null,
                    ex instanceof AccessDeniedException ? 403 : 400
            );
            throw ex;
        }
    }

    public Transfer updateTransferStatus(Long transferId, TransferStatus newStatus) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + transferId));

        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == Role.BASE_COMMANDER) {
            Long assignedBaseId = currentUser.getAssignedBaseId();
            boolean isSource = assignedBaseId != null && assignedBaseId.equals(transfer.getSourceBase().getId());
            boolean isTarget = assignedBaseId != null && assignedBaseId.equals(transfer.getTargetBase().getId());

            if (!isSource && !isTarget) {
                auditLogService.logAction(
                        "SECURITY_ACCESS_DENIED",
                        "/api/transfers/" + transferId + "/status",
                        "Base Commander (Base ID " + assignedBaseId + ") attempted unauthorized status change on transfer " + transfer.getTransferCode() + " belonging to Base IDs " + transfer.getSourceBase().getId() + "/" + transfer.getTargetBase().getId(),
                        assignedBaseId,
                        403
                );
                throw new AccessDeniedException("Access Denied: Base Commander cannot modify transfers belonging to other bases.");
            }
        }

        if (transfer.getStatus() == newStatus) {
            return transfer;
        }

        if (transfer.getStatus() == TransferStatus.COMPLETED || transfer.getStatus() == TransferStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of an already " + transfer.getStatus() + " transfer.");
        }

        if (newStatus == TransferStatus.COMPLETED) {
            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setCompletionDate(LocalDateTime.now());

            // Credit stock to target base
            BaseInventory targetInventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(transfer.getTargetBase().getId(), transfer.getEquipmentType().getId())
                    .orElseGet(() -> new BaseInventory(transfer.getTargetBase(), transfer.getEquipmentType(), 0, 0));

            targetInventory.setCurrentStock(targetInventory.getCurrentStock() + transfer.getQuantity());
            baseInventoryRepository.save(targetInventory);

            auditLogService.logAction(
                    "COMPLETE_TRANSFER",
                    "/api/transfers/" + transferId + "/status",
                    "Completed transfer " + transfer.getTransferCode() + " (" + transfer.getQuantity() + " " + transfer.getEquipmentType().getName() + " received at " + transfer.getTargetBase().getName() + ")",
                    transfer.getTargetBase().getId(),
                    200
            );
        } else if (newStatus == TransferStatus.CANCELLED) {
            transfer.setStatus(TransferStatus.CANCELLED);

            // Restore stock to source base
            BaseInventory sourceInventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(transfer.getSourceBase().getId(), transfer.getEquipmentType().getId())
                    .orElseThrow(() -> new IllegalStateException("Source base inventory record not found"));

            sourceInventory.setCurrentStock(sourceInventory.getCurrentStock() + transfer.getQuantity());
            baseInventoryRepository.save(sourceInventory);

            auditLogService.logAction(
                    "CANCEL_TRANSFER",
                    "/api/transfers/" + transferId + "/status",
                    "Cancelled transfer " + transfer.getTransferCode() + ", restored stock to " + transfer.getSourceBase().getName(),
                    transfer.getSourceBase().getId(),
                    200
            );
        }

        return transferRepository.save(transfer);
    }

    @Transactional(readOnly = true)
    public List<Transfer> getTransfers(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return transferRepository.filterTransfers(baseId, equipmentTypeId, startDateTime, endDateTime);
    }
}

package com.military.asset.service;

import com.military.asset.dto.AssignmentRequestDto;
import com.military.asset.entity.Assignment;
import com.military.asset.entity.AssignmentStatus;
import com.military.asset.entity.Base;
import com.military.asset.entity.BaseInventory;
import com.military.asset.entity.EquipmentType;
import com.military.asset.repository.AssignmentRepository;
import com.military.asset.repository.BaseInventoryRepository;
import com.military.asset.repository.BaseRepository;
import com.military.asset.repository.EquipmentTypeRepository;
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
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final BaseInventoryRepository baseInventoryRepository;
    private final AuditLogService auditLogService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             BaseRepository baseRepository,
                             EquipmentTypeRepository equipmentTypeRepository,
                             BaseInventoryRepository baseInventoryRepository,
                             AuditLogService auditLogService) {
        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.baseInventoryRepository = baseInventoryRepository;
        this.auditLogService = auditLogService;
    }

    public Assignment createAssignment(AssignmentRequestDto request) {
        try {
            if (request.getBaseId() == null) {
                throw new IllegalArgumentException("Base is required.");
            }
            if (request.getEquipmentTypeId() == null) {
                throw new IllegalArgumentException("Equipment Type is required.");
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Assignment quantity must be greater than 0.");
            }
            if (!StringUtils.hasText(request.getAssignedToPersonnel())) {
                throw new IllegalArgumentException("Assigned Personnel name is required.");
            }
            if (!StringUtils.hasText(request.getServiceId())) {
                throw new IllegalArgumentException("Service ID is required.");
            }

            // RBAC Enforcement
            SecurityUtils.checkBaseWritePermission(request.getBaseId());

            Base base = baseRepository.findById(request.getBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getBaseId()));

            EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment Type not found: " + request.getEquipmentTypeId()));

            BaseInventory inventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(base.getId(), equipmentType.getId())
                    .orElseThrow(() -> new IllegalStateException("No inventory record for asset at base"));

            int unassignedAvailable = inventory.getCurrentStock() - inventory.getAssignedCount();
            if (unassignedAvailable < request.getQuantity()) {
                throw new IllegalStateException("Cannot assign more than available unassigned stock. Available unassigned: " + unassignedAvailable);
            }

            Assignment assignment = new Assignment();
            assignment.setAssignmentCode("ASN-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            assignment.setBase(base);
            assignment.setEquipmentType(equipmentType);
            assignment.setAssignedToPersonnel(request.getAssignedToPersonnel());
            assignment.setServiceId(request.getServiceId());
            assignment.setRankTitle(request.getRankTitle());
            assignment.setQuantity(request.getQuantity());
            assignment.setExpectedReturnDate(request.getExpectedReturnDate());
            assignment.setStatus(AssignmentStatus.ACTIVE);
            assignment.setAssignmentDate(LocalDateTime.now());
            assignment.setNotes(request.getNotes());

            inventory.setAssignedCount(inventory.getAssignedCount() + request.getQuantity());
            baseInventoryRepository.save(inventory);

            Assignment savedAssignment = assignmentRepository.save(assignment);

            auditLogService.logAction(
                    "ASSIGN_ASSET",
                    "/api/assignments",
                    "Assigned " + request.getQuantity() + " " + equipmentType.getName() + " to " + request.getRankTitle() + " " + request.getAssignedToPersonnel() + " (" + request.getServiceId() + ") at " + base.getName(),
                    base.getId(),
                    201
            );

            return savedAssignment;
        } catch (Exception ex) {
            auditLogService.logAction(
                    "CREATE_ASSIGNMENT_FAILED",
                    "/api/assignments",
                    "Failed assignment creation attempt: " + ex.getMessage(),
                    request != null ? request.getBaseId() : null,
                    ex instanceof AccessDeniedException ? 403 : 400
            );
            throw ex;
        }
    }

    public Assignment returnAssignment(Long assignmentId) {
        try {
            Assignment assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

            SecurityUtils.checkBaseWritePermission(assignment.getBase().getId());

            if (assignment.getStatus() == AssignmentStatus.RETURNED) {
                return assignment;
            }

            assignment.setStatus(AssignmentStatus.RETURNED);

            BaseInventory inventory = baseInventoryRepository.findByBaseIdAndEquipmentTypeId(assignment.getBase().getId(), assignment.getEquipmentType().getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory record not found"));

            inventory.setAssignedCount(Math.max(0, inventory.getAssignedCount() - assignment.getQuantity()));
            baseInventoryRepository.save(inventory);

            auditLogService.logAction(
                    "RETURN_ASSIGNMENT",
                    "/api/assignments/" + assignmentId + "/return",
                    "Returned assigned assets for assignment code " + assignment.getAssignmentCode(),
                    assignment.getBase().getId(),
                    200
            );

            return assignmentRepository.save(assignment);
        } catch (Exception ex) {
            auditLogService.logAction(
                    "RETURN_ASSIGNMENT_FAILED",
                    "/api/assignments/" + assignmentId + "/return",
                    "Failed return assignment attempt: " + ex.getMessage(),
                    null,
                    ex instanceof AccessDeniedException ? 403 : 400
            );
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(Long requestedBaseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        Long baseId = SecurityUtils.validateAndGetBaseScope(requestedBaseId);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return assignmentRepository.filterAssignments(baseId, equipmentTypeId, startDateTime, endDateTime);
    }
}

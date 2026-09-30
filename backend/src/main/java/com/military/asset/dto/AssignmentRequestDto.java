package com.military.asset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AssignmentRequestDto {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotBlank(message = "Assigned Personnel name is required")
    private String assignedToPersonnel;

    @NotBlank(message = "Service ID is required")
    private String serviceId;

    @NotBlank(message = "Rank Title is required")
    private String rankTitle;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private LocalDate expectedReturnDate;

    private String notes;

    public AssignmentRequestDto() {}

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public String getAssignedToPersonnel() { return assignedToPersonnel; }
    public void setAssignedToPersonnel(String assignedToPersonnel) { this.assignedToPersonnel = assignedToPersonnel; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getRankTitle() { return rankTitle; }
    public void setRankTitle(String rankTitle) { this.rankTitle = rankTitle; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

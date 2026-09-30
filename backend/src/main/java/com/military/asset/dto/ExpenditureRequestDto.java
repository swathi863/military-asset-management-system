package com.military.asset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpenditureRequestDto {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Reason is required")
    private String reason; // TRAINING, OPERATIONAL_USAGE, DAMAGED_DISPOSAL, EXPIRED

    private String operationName;

    private String remarks;

    public ExpenditureRequestDto() {}

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

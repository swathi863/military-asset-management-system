package com.military.asset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransferRequestDto {

    @NotNull(message = "Source Base ID is required")
    private Long sourceBaseId;

    @NotNull(message = "Target Base ID is required")
    private Long targetBaseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String remarks;

    public TransferRequestDto() {}

    public Long getSourceBaseId() { return sourceBaseId; }
    public void setSourceBaseId(Long sourceBaseId) { this.sourceBaseId = sourceBaseId; }

    public Long getTargetBaseId() { return targetBaseId; }
    public void setTargetBaseId(Long targetBaseId) { this.targetBaseId = targetBaseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

package com.military.asset.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PurchaseRequestDto {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Equipment Type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit Cost is required")
    @Min(value = 0, message = "Unit cost cannot be negative")
    private BigDecimal unitCost;

    @NotBlank(message = "Vendor Name is required")
    private String vendorName;

    private String remarks;

    public PurchaseRequestDto() {}

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

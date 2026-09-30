package com.military.asset.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_inventories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"base_id", "equipment_type_id"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BaseInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(name = "opening_balance", nullable = false)
    private Integer openingBalance = 0;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "assigned_count", nullable = false)
    private Integer assignedCount = 0;

    @Column(name = "expended_count", nullable = false)
    private Integer expendedCount = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public BaseInventory() {}

    public BaseInventory(Base base, EquipmentType equipmentType, Integer openingBalance, Integer currentStock) {
        this.base = base;
        this.equipmentType = equipmentType;
        this.openingBalance = openingBalance;
        this.currentStock = currentStock;
        this.assignedCount = 0;
        this.expendedCount = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Base getBase() { return base; }
    public void setBase(Base base) { this.base = base; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public Integer getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Integer openingBalance) { this.openingBalance = openingBalance; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getAssignedCount() { return assignedCount; }
    public void setAssignedCount(Integer assignedCount) { this.assignedCount = assignedCount; }

    public Integer getExpendedCount() { return expendedCount; }
    public void setExpendedCount(Integer expendedCount) { this.expendedCount = expendedCount; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}

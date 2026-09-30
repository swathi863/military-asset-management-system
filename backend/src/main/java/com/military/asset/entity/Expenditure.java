package com.military.asset.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures")
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expenditure_code", nullable = false, unique = true, length = 50)
    private String expenditureCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "expenditure_date", nullable = false)
    private LocalDateTime expenditureDate;

    @Column(nullable = false, length = 100)
    private String reason; // TRAINING, OPERATIONAL_USAGE, DAMAGED_DISPOSAL, EXPIRED

    @Column(name = "operation_name", length = 100)
    private String operationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public Expenditure() {}

    @PrePersist
    protected void onCreate() {
        if (this.expenditureDate == null) {
            this.expenditureDate = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExpenditureCode() { return expenditureCode; }
    public void setExpenditureCode(String expenditureCode) { this.expenditureCode = expenditureCode; }

    public Base getBase() { return base; }
    public void setBase(Base base) { this.base = base; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getExpenditureDate() { return expenditureDate; }
    public void setExpenditureDate(LocalDateTime expenditureDate) { this.expenditureDate = expenditureDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }

    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

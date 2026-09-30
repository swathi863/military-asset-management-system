package com.military.asset.repository;

import com.military.asset.entity.BaseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseInventoryRepository extends JpaRepository<BaseInventory, Long> {
    Optional<BaseInventory> findByBaseIdAndEquipmentTypeId(Long baseId, Long equipmentTypeId);
    List<BaseInventory> findByBaseId(Long baseId);
    List<BaseInventory> findByEquipmentTypeId(Long equipmentTypeId);
}

package com.military.asset.repository;

import com.military.asset.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
    List<EquipmentType> findByCategory(String category);
}

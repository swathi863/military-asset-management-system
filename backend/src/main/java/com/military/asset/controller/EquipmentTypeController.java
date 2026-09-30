package com.military.asset.controller;

import com.military.asset.entity.EquipmentType;
import com.military.asset.repository.EquipmentTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
public class EquipmentTypeController {

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeController(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentType>> getAllEquipmentTypes() {
        return ResponseEntity.ok(equipmentTypeRepository.findAll());
    }
}

package com.military.asset.controller;

import com.military.asset.dto.ExpenditureRequestDto;
import com.military.asset.entity.Expenditure;
import com.military.asset.service.ExpenditureService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping
    public ResponseEntity<Expenditure> recordExpenditure(@Valid @RequestBody ExpenditureRequestDto request) {
        Expenditure expenditure = expenditureService.recordExpenditure(request);
        return new ResponseEntity<>(expenditure, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Expenditure>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenditureService.getExpenditures(baseId, equipmentTypeId, startDate, endDate));
    }
}

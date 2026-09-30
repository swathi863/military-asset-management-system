package com.military.asset.controller;

import com.military.asset.dto.PurchaseRequestDto;
import com.military.asset.entity.Purchase;
import com.military.asset.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<Purchase> recordPurchase(@Valid @RequestBody PurchaseRequestDto request) {
        Purchase purchase = purchaseService.recordPurchase(request);
        return new ResponseEntity<>(purchase, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(purchaseService.getPurchases(baseId, equipmentTypeId, startDate, endDate));
    }
}

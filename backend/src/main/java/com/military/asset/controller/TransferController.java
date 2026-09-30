package com.military.asset.controller;

import com.military.asset.dto.TransferRequestDto;
import com.military.asset.entity.Transfer;
import com.military.asset.entity.TransferStatus;
import com.military.asset.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Transfer> initiateTransfer(@Valid @RequestBody TransferRequestDto request) {
        Transfer transfer = transferService.initiateTransfer(request);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Transfer> updateTransferStatus(
            @PathVariable Long id,
            @RequestParam TransferStatus status) {
        return ResponseEntity.ok(transferService.updateTransferStatus(id, status));
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> getTransfers(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transferService.getTransfers(baseId, equipmentTypeId, startDate, endDate));
    }
}

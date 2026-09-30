package com.military.asset.controller;

import com.military.asset.dto.AssignmentRequestDto;
import com.military.asset.entity.Assignment;
import com.military.asset.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody AssignmentRequestDto request) {
        Assignment assignment = assignmentService.createAssignment(request);
        return new ResponseEntity<>(assignment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Assignment> returnAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.returnAssignment(id));
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(assignmentService.getAssignments(baseId, equipmentTypeId, startDate, endDate));
    }
}

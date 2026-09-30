package com.military.asset.controller;

import com.military.asset.entity.Base;
import com.military.asset.repository.BaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseRepository baseRepository;

    public BaseController(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Base>> getAllBases() {
        return ResponseEntity.ok(baseRepository.findAll());
    }
}

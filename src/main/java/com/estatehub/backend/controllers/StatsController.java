package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.StatsSummaryDTO;
import com.estatehub.backend.services.IStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final IStatsService statsService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<StatsSummaryDTO> getSummary() {
        return ResponseEntity.ok(statsService.getSummary());
    }
}

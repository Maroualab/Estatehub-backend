package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.DashboardStatsDTO;
import com.estatehub.backend.services.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    /** GET /api/dashboard/stats — KPIs du bailleur connecté */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}

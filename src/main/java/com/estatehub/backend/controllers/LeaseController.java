package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.CreateLeaseRequest;
import com.estatehub.backend.dtos.LeaseDTO;
import com.estatehub.backend.services.ILeaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final ILeaseService leaseService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<LeaseDTO> createLease(@Valid @RequestBody CreateLeaseRequest request) {
        return ResponseEntity.ok(leaseService.createLease(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<LeaseDTO>> getLeasesForLandlord() {
        return ResponseEntity.ok(leaseService.getLeasesForLandlord());
    }

    /** Tâche 4 — Bail actif du locataire connecté */
    @GetMapping("/my-lease")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<LeaseDTO> getMyActiveLease() {
        return ResponseEntity.ok(leaseService.getMyActiveLease());
    }

    /** Tâche 5 — Résiliation logique (status → TERMINATED) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> terminateLease(@PathVariable Long id) {
        leaseService.terminateLease(id);
        return ResponseEntity.noContent().build();
    }

    /** Tâche 6 — Bail actif pour une unité donnée */
    @GetMapping("/unit/{unitId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<LeaseDTO> getLeaseByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(leaseService.getLeaseByUnit(unitId));
    }
}

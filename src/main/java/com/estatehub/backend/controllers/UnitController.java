package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.CreateUnitRequest;
import com.estatehub.backend.dtos.UnitDTO;
import com.estatehub.backend.dtos.UpdateUnitRequest;
import com.estatehub.backend.services.IUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final IUnitService unitService;

    /** POST /api/units — Ajouter un appartement */
    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<UnitDTO> createUnit(@Valid @RequestBody CreateUnitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.createUnit(request));
    }

    /** GET /api/units/building/{buildingId} — Lister les appartements d'un immeuble */
    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<UnitDTO>> getUnitsByBuilding(@PathVariable Long buildingId) {
        return ResponseEntity.ok(unitService.getUnitsByBuilding(buildingId));
    }

    /** GET /api/units/{id} — Détail d'un appartement */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<UnitDTO> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getUnitById(id));
    }

    /** PUT /api/units/{id} — Mettre à jour un appartement */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<UnitDTO> updateUnit(@PathVariable Long id,
                                              @Valid @RequestBody UpdateUnitRequest request) {
        return ResponseEntity.ok(unitService.updateUnit(id, request));
    }

    /** DELETE /api/units/{id} — Supprimer un appartement */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}

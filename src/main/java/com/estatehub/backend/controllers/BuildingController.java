package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.BuildingDTO;
import com.estatehub.backend.dtos.CreateBuildingRequest;
import com.estatehub.backend.dtos.UpdateBuildingRequest;
import com.estatehub.backend.services.IBuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final IBuildingService buildingService;

    /** POST /api/buildings — Créer un immeuble */
    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<BuildingDTO> createBuilding(@Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingService.createBuilding(request));
    }

    /** GET /api/buildings — Lister mes immeubles */
    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<BuildingDTO>> getMyBuildings() {
        return ResponseEntity.ok(buildingService.getBuildingsForLandlord());
    }

    /** GET /api/buildings/{id} — Détail d'un immeuble */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<BuildingDTO> getBuildingById(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.getBuildingById(id));
    }

    /** PUT /api/buildings/{id} — Mettre à jour un immeuble */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<BuildingDTO> updateBuilding(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateBuildingRequest request) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, request));
    }

    /** DELETE /api/buildings/{id} — Supprimer un immeuble */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}

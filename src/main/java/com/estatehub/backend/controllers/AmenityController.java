package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.AmenityDTO;
import com.estatehub.backend.dtos.CreateAmenityRequest;
import com.estatehub.backend.dtos.UpdateBuildingAmenitiesRequest;
import com.estatehub.backend.services.IAmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final IAmenityService amenityService;

    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<AmenityDTO>> getAllAmenitiesForLandlord() {
        return ResponseEntity.ok(amenityService.getAllAmenitiesForLandlord());
    }

    @GetMapping("/building/{buildingId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<AmenityDTO>> getByBuilding(
            @PathVariable Long buildingId) {
        return ResponseEntity.ok(amenityService.getAmenitiesByBuilding(buildingId));
    }

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<AmenityDTO> createAmenity(@Valid @RequestBody CreateAmenityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(amenityService.createAmenity(request));
    }

    @PutMapping("/building/{buildingId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<AmenityDTO>> updateBuildingAmenities(
            @PathVariable Long buildingId,
            @Valid @RequestBody UpdateBuildingAmenitiesRequest request) {
        return ResponseEntity.ok(amenityService.updateBuildingAmenities(buildingId, request.getAmenityIds()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}

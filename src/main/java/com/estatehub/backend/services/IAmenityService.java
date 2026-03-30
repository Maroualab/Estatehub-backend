package com.estatehub.backend.services;

import com.estatehub.backend.dtos.AmenityDTO;
import com.estatehub.backend.dtos.CreateAmenityRequest;

import java.util.List;

public interface IAmenityService {
    List<AmenityDTO> getAllAmenitiesForLandlord();
    List<AmenityDTO> getAmenitiesByBuilding(Long buildingId);
    AmenityDTO createAmenity(CreateAmenityRequest request);
    void deleteAmenity(Long id);
    List<AmenityDTO> updateBuildingAmenities(Long buildingId, List<Long> amenityIds);
}

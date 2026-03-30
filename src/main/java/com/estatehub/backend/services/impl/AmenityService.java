package com.estatehub.backend.services.impl;

import com.estatehub.backend.dtos.AmenityDTO;
import com.estatehub.backend.dtos.CreateAmenityRequest;
import com.estatehub.backend.exceptions.BusinessValidationException;
import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.models.Amenity;
import com.estatehub.backend.models.Building;
import com.estatehub.backend.repositories.AmenityRepository;
import com.estatehub.backend.repositories.BuildingRepository;
import com.estatehub.backend.services.IAmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService implements IAmenityService {

    private final AmenityRepository amenityRepository;
    private final BuildingRepository buildingRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Building getOwnedBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building", buildingId));
        if (!building.getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Acces refuse : cet immeuble ne vous appartient pas.");
        }
        return building;
    }

    private AmenityDTO toDto(Amenity amenity) {
        return AmenityDTO.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .monthlyPrice(amenity.getMonthlyPrice())
                .buildingId(amenity.getBuilding().getId())
                .build();
    }

    public List<AmenityDTO> getAllAmenitiesForLandlord() {
        return amenityRepository.findByBuildingLandlordEmail(getCurrentUserEmail())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<AmenityDTO> getAmenitiesByBuilding(Long buildingId) {
        getOwnedBuilding(buildingId);
        return amenityRepository.findByBuildingId(buildingId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AmenityDTO createAmenity(CreateAmenityRequest request) {
        Building building = getOwnedBuilding(request.getBuildingId());

        Amenity amenity = Amenity.builder()
                .name(request.getName().trim())
                .monthlyPrice(request.getMonthlyPrice() == null ? BigDecimal.ZERO : request.getMonthlyPrice())
                .building(building)
                .build();

        return toDto(amenityRepository.save(amenity));
    }

    @Transactional
    public void deleteAmenity(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", id));

        if (!amenity.getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Acces refuse : cet equipement ne vous appartient pas.");
        }

        amenityRepository.deleteById(id);
    }

    @Transactional
    public List<AmenityDTO> updateBuildingAmenities(Long buildingId, List<Long> amenityIds) {
        Building building = getOwnedBuilding(buildingId);

        List<Amenity> templates = amenityIds == null || amenityIds.isEmpty()
                ? List.of()
                : amenityRepository.findAllById(amenityIds);

        if (templates.size() != (amenityIds == null ? 0 : amenityIds.size())) {
            throw new BusinessValidationException("Un ou plusieurs equipements selectionnes sont introuvables.");
        }

        amenityRepository.deleteByBuildingId(buildingId);

        List<Amenity> saved = templates.stream()
                .map(a -> Amenity.builder()
                        .name(a.getName())
                        .monthlyPrice(a.getMonthlyPrice())
                        .building(building)
                        .build())
                .map(amenityRepository::save)
                .toList();

        return saved.stream().map(this::toDto).toList();
    }
}

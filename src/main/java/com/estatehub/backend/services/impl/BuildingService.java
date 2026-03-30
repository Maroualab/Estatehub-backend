package com.estatehub.backend.services.impl;

import com.estatehub.backend.dtos.BuildingDTO;
import com.estatehub.backend.dtos.CreateBuildingRequest;
import com.estatehub.backend.dtos.UpdateBuildingRequest;
import com.estatehub.backend.exceptions.BusinessValidationException;
import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.mappers.BuildingMapper;
import com.estatehub.backend.models.Building;
import com.estatehub.backend.models.User;
import com.estatehub.backend.repositories.BuildingRepository;
import com.estatehub.backend.repositories.UserRepository;
import com.estatehub.backend.services.IBuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService implements IBuildingService {

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final BuildingMapper buildingMapper;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Building getOwnedBuilding(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));
        if (!building.getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : cet immeuble ne vous appartient pas !");
        }
        return building;
    }

    public BuildingDTO createBuilding(CreateBuildingRequest request) {
        User landlord = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", getCurrentUserEmail()));

        Building building = Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .landlord(landlord)
                .build();

        buildingRepository.save(building);
        return buildingMapper.toDto(building);
    }

    public List<BuildingDTO> getBuildingsForLandlord() {
        User user = userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", getCurrentUserEmail()));
        return buildingRepository.findByLandlordId(user.getId())
                .stream()
                .map(buildingMapper::toDto)
                .toList();
    }

    public BuildingDTO getBuildingById(Long id) {
        return buildingMapper.toDto(getOwnedBuilding(id));
    }

    @Transactional
    public BuildingDTO updateBuilding(Long id, UpdateBuildingRequest request) {
        Building building = getOwnedBuilding(id);
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        building.setCity(request.getCity());
        building.setZipCode(request.getZipCode());
        return buildingMapper.toDto(buildingRepository.save(building));
    }

    @Transactional
    public void deleteBuilding(Long id) {
        getOwnedBuilding(id); 
        buildingRepository.deleteById(id);
    }
}

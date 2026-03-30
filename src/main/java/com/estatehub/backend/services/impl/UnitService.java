package com.estatehub.backend.services.impl;

import com.estatehub.backend.dtos.CreateUnitRequest;
import com.estatehub.backend.dtos.UnitDTO;
import com.estatehub.backend.dtos.UpdateUnitRequest;
import com.estatehub.backend.exceptions.BusinessValidationException;
import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.mappers.UnitMapper;
import com.estatehub.backend.models.Building;
import com.estatehub.backend.models.Unit;
import com.estatehub.backend.models.enums.LeaseStatus;
import com.estatehub.backend.models.enums.UnitStatus;
import com.estatehub.backend.repositories.BuildingRepository;
import com.estatehub.backend.repositories.LeaseRepository;
import com.estatehub.backend.repositories.UnitRepository;
import com.estatehub.backend.services.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {

    private final UnitRepository unitRepository;
    private final BuildingRepository buildingRepository;
    private final LeaseRepository leaseRepository;
    private final UnitMapper unitMapper;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** Dérive le statut d'un DTO à partir d'un ensemble d'IDs occupés pré-chargé. */
    private UnitDTO enrichStatus(UnitDTO dto, Set<Long> occupiedIds) {
        dto.setStatus(occupiedIds.contains(dto.getId()) ? UnitStatus.OCCUPIED : UnitStatus.VACANT);
        return dto;
    }

    /** Dérive le statut d'un DTO par appel individuel (utilisé pour les accès unitaires). */
    private UnitDTO enrichStatus(UnitDTO dto) {
        boolean occupied = leaseRepository.existsByUnitIdAndStatus(dto.getId(), LeaseStatus.ACTIVE);
        dto.setStatus(occupied ? UnitStatus.OCCUPIED : UnitStatus.VACANT);
        return dto;
    }

    private Building getOwnedBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building", buildingId));
        if (!building.getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : cet immeuble ne vous appartient pas !");
        }
        return building;
    }

    private Unit getOwnedUnit(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", unitId));
        if (!unit.getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : cet appartement ne vous appartient pas !");
        }
        return unit;
    }

    // ------------------------------------------------------------------ CREATE
    public UnitDTO createUnit(CreateUnitRequest request) {
        Building building = getOwnedBuilding(request.getBuildingId());

        Unit unit = Unit.builder()
                .doorNumber(request.getDoorNumber())
                .floor(request.getFloor())
                .unitType(request.getUnitType())
                .rentPrice(request.getRentPrice())
                .building(building)
                .build();

        unitRepository.save(unit);
        return enrichStatus(unitMapper.toDto(unit));
    }

    // ------------------------------------------------------------------ READ ALL (by building)
    public List<UnitDTO> getUnitsByBuilding(Long buildingId) {
        getOwnedBuilding(buildingId); // ownership check
        Set<Long> occupiedIds = leaseRepository.findOccupiedUnitIdsByBuildingId(buildingId);
        return unitRepository.findByBuildingId(buildingId)
                .stream()
                .map(unitMapper::toDto)
                .map(dto -> enrichStatus(dto, occupiedIds))
                .toList();
    }

    // ------------------------------------------------------------------ READ ONE
    public UnitDTO getUnitById(Long id) {
        return enrichStatus(unitMapper.toDto(getOwnedUnit(id)));
    }

    // ------------------------------------------------------------------ UPDATE
    @Transactional
    public UnitDTO updateUnit(Long id, UpdateUnitRequest request) {
        Unit unit = getOwnedUnit(id);
        unit.setDoorNumber(request.getDoorNumber());
        unit.setFloor(request.getFloor());
        unit.setUnitType(request.getUnitType());
        unit.setRentPrice(request.getRentPrice());
        return enrichStatus(unitMapper.toDto(unitRepository.save(unit)));
    }

    // ------------------------------------------------------------------ DELETE
    @Transactional
    public void deleteUnit(Long id) {
        getOwnedUnit(id); // ownership check
        unitRepository.deleteById(id);
    }
}

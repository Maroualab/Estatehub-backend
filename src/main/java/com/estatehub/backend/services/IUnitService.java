package com.estatehub.backend.services;

import com.estatehub.backend.dtos.CreateUnitRequest;
import com.estatehub.backend.dtos.UnitDTO;
import com.estatehub.backend.dtos.UpdateUnitRequest;

import java.util.List;

public interface IUnitService {
    UnitDTO createUnit(CreateUnitRequest request);
    List<UnitDTO> getUnitsByBuilding(Long buildingId);
    UnitDTO getUnitById(Long id);
    UnitDTO updateUnit(Long id, UpdateUnitRequest request);
    void deleteUnit(Long id);
}

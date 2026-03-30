package com.estatehub.backend.services;

import com.estatehub.backend.dtos.BuildingDTO;
import com.estatehub.backend.dtos.CreateBuildingRequest;
import com.estatehub.backend.dtos.UpdateBuildingRequest;

import java.util.List;

public interface IBuildingService {
    BuildingDTO createBuilding(CreateBuildingRequest request);
    List<BuildingDTO> getBuildingsForLandlord();
    BuildingDTO getBuildingById(Long id);
    BuildingDTO updateBuilding(Long id, UpdateBuildingRequest request);
    void deleteBuilding(Long id);
}

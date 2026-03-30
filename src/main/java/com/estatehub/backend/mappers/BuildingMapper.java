package com.estatehub.backend.mappers;

import com.estatehub.backend.dtos.BuildingDTO;
import com.estatehub.backend.models.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    @Mapping(source = "landlord.id", target = "landlordId")
    BuildingDTO toDto(Building building);
}

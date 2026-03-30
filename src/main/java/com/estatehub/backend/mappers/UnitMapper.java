package com.estatehub.backend.mappers;

import com.estatehub.backend.dtos.UnitDTO;
import com.estatehub.backend.models.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    @Mapping(source = "building.id", target = "buildingId")
    @Mapping(target = "status", ignore = true) // enrichi par UnitService après mapping
    UnitDTO toDto(Unit unit);
}

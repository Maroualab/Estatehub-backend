package com.estatehub.backend.mappers;

import com.estatehub.backend.dtos.LeaseDTO;
import com.estatehub.backend.models.Lease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaseMapper {

    @Mapping(source = "tenant.id", target = "tenantId")
    @Mapping(source = "tenant.email", target = "tenantEmail")
    @Mapping(source = "tenant.firstName", target = "tenantFirstName")
    @Mapping(source = "tenant.lastName", target = "tenantLastName")
    @Mapping(source = "unit.id", target = "unitId")
    @Mapping(source = "unit.doorNumber", target = "unitDoorNumber")
    @Mapping(source = "unit.building.name", target = "buildingName")
    @Mapping(expression = "java(lease.getTotalMonthlyPayment())", target = "totalMonthlyPayment")
    LeaseDTO toDto(Lease lease);
}

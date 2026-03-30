package com.estatehub.backend.mappers;

import com.estatehub.backend.dtos.InvoiceDTO;
import com.estatehub.backend.models.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "lease.id",                   target = "leaseId")
    @Mapping(source = "lease.tenant.email",          target = "tenantEmail")
    @Mapping(source = "lease.unit.doorNumber",       target = "unitDoorNumber")
    @Mapping(source = "lease.unit.building.name",    target = "buildingName")
    @Mapping(source = "lease.baseRentAmount",        target = "leaseBaseRentAmount")
    @Mapping(source = "lease.utilityAmount",         target = "leaseUtilityAmount")
    @Mapping(
        expression = "java(invoice.getLease().getTenant().getFirstName() + \" \" + invoice.getLease().getTenant().getLastName())",
        target = "tenantFullName"
    )
    InvoiceDTO toDto(Invoice invoice);
}

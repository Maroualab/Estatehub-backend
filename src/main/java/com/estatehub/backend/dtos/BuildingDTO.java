package com.estatehub.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String zipCode;
    private Long landlordId;
}
package com.estatehub.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private String doorNumber;
    private Integer floor;
    private Long buildingId;
}
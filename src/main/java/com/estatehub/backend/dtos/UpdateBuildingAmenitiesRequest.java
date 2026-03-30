package com.estatehub.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBuildingAmenitiesRequest {

    @NotNull(message = "amenityIds is required")
    private List<Long> amenityIds;
}

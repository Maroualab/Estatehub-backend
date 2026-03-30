package com.estatehub.backend.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAmenityRequest {

    @NotBlank(message = "Amenity name is required")
    private String name;

    @NotNull(message = "Building id is required")
    private Long buildingId;

    @Builder.Default
    @DecimalMin(value = "0.0", inclusive = true, message = "Monthly price cannot be negative")
    private BigDecimal monthlyPrice = BigDecimal.ZERO;
}

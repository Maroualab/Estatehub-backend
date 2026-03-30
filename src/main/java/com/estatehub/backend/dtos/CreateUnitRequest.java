package com.estatehub.backend.dtos;

import com.estatehub.backend.models.enums.UnitType;
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
public class CreateUnitRequest {

    @NotBlank(message = "Le numéro de porte est obligatoire")
    private String doorNumber;

    private Integer floor;

    @NotNull(message = "Le type d'appartement est obligatoire")
    private UnitType unitType;

    @NotNull(message = "Le prix du loyer est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le loyer doit être supérieur à 0")
    private BigDecimal rentPrice;

    @NotNull(message = "L'identifiant de l'immeuble est obligatoire")
    private Long buildingId;
}

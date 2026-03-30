package com.estatehub.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaseRequest {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Le montant du loyer de base est obligatoire")
    @Positive(message = "Le loyer de base doit être supérieur à 0")
    private BigDecimal baseRentAmount;

    @NotNull(message = "Le montant des charges est obligatoire")
    @PositiveOrZero(message = "Le montant des charges ne peut pas être négatif")
    private BigDecimal utilityAmount;

    @NotNull(message = "L'identifiant de l'appartement est obligatoire")
    private Long unitId;

    @NotBlank(message = "L'email du locataire est obligatoire")
    @Email(message = "Format d'email du locataire invalide")
    private String tenantEmail;
}

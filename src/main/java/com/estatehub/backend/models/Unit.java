package com.estatehub.backend.models;

import com.estatehub.backend.models.enums.UnitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "units")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Door number is required")
    @Column(name = "door_number", nullable = false)
    private String doorNumber;

    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unitType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Le loyer doit être supérieur à 0")
    @Column(nullable = false)
    private BigDecimal rentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
}

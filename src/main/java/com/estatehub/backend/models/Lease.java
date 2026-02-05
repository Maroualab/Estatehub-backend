package com.estatehub.backend.models;

import com.estatehub.backend.models.enums.LeaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "leases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseRentAmount;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal utilityAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaseStatus status;

    // --- RELATIONS ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;


    public BigDecimal getTotalMonthlyPayment() {
        return baseRentAmount.add(utilityAmount);
    }
}
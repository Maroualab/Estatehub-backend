package com.estatehub.backend.dtos;

import com.estatehub.backend.models.enums.LeaseStatus;
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
public class LeaseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal baseRentAmount;
    private BigDecimal utilityAmount;
    private BigDecimal totalMonthlyPayment;

    private LeaseStatus status;

    private Long tenantId;
    private String tenantEmail;

    private Long unitId;
    private String unitDoorNumber;
}
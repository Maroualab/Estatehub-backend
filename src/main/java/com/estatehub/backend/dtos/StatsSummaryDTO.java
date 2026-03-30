package com.estatehub.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsSummaryDTO {
    private double occupancyRate;
    private BigDecimal theoreticalMonthlyRevenue;
    private BigDecimal actualMonthlyRevenue;
    private double portfolioGrowth;
    private List<MonthlyRevenuePointDTO> monthlyRevenueSeries;
}

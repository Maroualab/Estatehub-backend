package com.estatehub.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalBuildings;
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;
    /** Taux d'occupation en % (0.0 – 100.0, arrondi à 1 décimale) */
    private double occupancyRate;
}

package com.estatehub.backend.services.impl;

import com.estatehub.backend.dtos.DashboardStatsDTO;
import com.estatehub.backend.models.enums.LeaseStatus;
import com.estatehub.backend.repositories.BuildingRepository;
import com.estatehub.backend.repositories.LeaseRepository;
import com.estatehub.backend.repositories.UnitRepository;
import com.estatehub.backend.services.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final BuildingRepository buildingRepository;
    private final UnitRepository     unitRepository;
    private final LeaseRepository    leaseRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Calcule les KPIs du dashboard pour le bailleur connecté :
     * - nombre d'immeubles
     * - nombre total d'unités
     * - unités occupées / vacantes
     * - taux d'occupation (%)
     *
     * Chaque métrique utilise une requête SQL dédiée → 3 requêtes au total, pas de N+1.
     */
    public DashboardStatsDTO getStats() {
        String email = getCurrentUserEmail();

        long totalBuildings  = buildingRepository.countByLandlordEmail(email);
        long totalUnits      = unitRepository.countByBuildingLandlordEmail(email);
        long occupiedUnits   = leaseRepository.countOccupiedUnitsByLandlordEmail(email, LeaseStatus.ACTIVE);
        long vacantUnits     = totalUnits - occupiedUnits;
        double occupancyRate = totalUnits == 0
                ? 0.0
                : Math.round(occupiedUnits * 1000.0 / totalUnits) / 10.0;

        return DashboardStatsDTO.builder()
                .totalBuildings(totalBuildings)
                .totalUnits(totalUnits)
                .occupiedUnits(occupiedUnits)
                .vacantUnits(vacantUnits)
                .occupancyRate(occupancyRate)
                .build();
    }
}

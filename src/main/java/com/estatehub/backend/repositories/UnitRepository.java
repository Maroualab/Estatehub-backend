package com.estatehub.backend.repositories;

import com.estatehub.backend.models.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByBuildingId(Long buildingId);

    /** Total des unités d'un bailleur — utilisé dans les KPIs du dashboard */
    long countByBuildingLandlordEmail(String landlordEmail);
}

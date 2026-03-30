package com.estatehub.backend.repositories;

import com.estatehub.backend.models.Lease;
import com.estatehub.backend.models.enums.LeaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    List<Lease> findByTenantId(Long tenantId);
    List<Lease> findByUnitId(Long unitId);
    List<Lease> findByUnitBuildingLandlordEmail(String email);
       List<Lease> findByStatus(LeaseStatus status);

    /**
     * Vérifie l'existence d'un bail actif sur une unité donnée.
     * Utilisé pour bloquer la création de deux baux ACTIVE sur la même unité.
     */
    boolean existsByUnitIdAndStatus(Long unitId, LeaseStatus status);

    /** Bail actif d'un locataire (pour GET /my-lease) */
    Optional<Lease> findByTenantEmailAndStatus(String tenantEmail, LeaseStatus status);

    /** Bail actif sur une unité précise (pour GET /unit/{unitId}) */
    Optional<Lease> findByUnitIdAndStatus(Long unitId, LeaseStatus status);

    /**
     * Retourne les IDs des unités OCCUPÉES dans un immeuble.
     * Utilisé pour enrichir le statut des UnitDTO en un seul appel (évite le N+1).
     */
    @Query("SELECT l.unit.id FROM Lease l WHERE l.unit.building.id = :buildingId AND l.status = 'ACTIVE'")
    Set<Long> findOccupiedUnitIdsByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * Compte les unités occupées (bail ACTIVE) pour un bailleur — utilisé dans les KPIs.
     */
    @Query("SELECT COUNT(DISTINCT l.unit.id) FROM Lease l " +
           "WHERE l.unit.building.landlord.email = :landlordEmail AND l.status = :status")
    long countOccupiedUnitsByLandlordEmail(
            @Param("landlordEmail") String landlordEmail,
            @Param("status") LeaseStatus status);

    @Query("SELECT COALESCE(SUM(l.baseRentAmount + l.utilityAmount), 0) FROM Lease l " +
          "WHERE l.unit.building.landlord.email = :landlordEmail AND l.status = :status")
    BigDecimal sumTotalMonthlyPaymentByLandlordEmailAndStatus(
           @Param("landlordEmail") String landlordEmail,
           @Param("status") LeaseStatus status);
}

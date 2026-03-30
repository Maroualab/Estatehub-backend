package com.estatehub.backend.repositories;

import com.estatehub.backend.models.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByLandlordId(Long landlordId);

    /** Nombre d'immeubles d'un bailleur — utilisé dans les KPIs du dashboard */
    long countByLandlordEmail(String landlordEmail);
}

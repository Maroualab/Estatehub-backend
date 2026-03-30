package com.estatehub.backend.repositories;

import com.estatehub.backend.models.Amenity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
        List<Amenity> findByBuildingId(Long buildingId);

        List<Amenity> findByBuildingLandlordEmail(String email);

        void deleteByBuildingId(Long buildingId);

}

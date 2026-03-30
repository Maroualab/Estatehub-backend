package com.estatehub.backend.services;

import com.estatehub.backend.dtos.CreateLeaseRequest;
import com.estatehub.backend.dtos.LeaseDTO;
import com.estatehub.backend.exceptions.BusinessValidationException;
import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.mappers.LeaseMapper;
import com.estatehub.backend.models.Lease;
import com.estatehub.backend.models.Unit;
import com.estatehub.backend.models.User;
import com.estatehub.backend.models.enums.LeaseStatus;
import com.estatehub.backend.repositories.LeaseRepository;
import com.estatehub.backend.repositories.UnitRepository;
import com.estatehub.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaseService implements ILeaseService {

    private final LeaseRepository leaseRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final LeaseMapper leaseMapper;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public LeaseDTO createLease(CreateLeaseRequest request) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit", request.getUnitId()));

        if (!unit.getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : cet appartement ne vous appartient pas !");
        }

        // Vérification métier : impossible de créer un bail sur une unité déjà louée
        if (leaseRepository.existsByUnitIdAndStatus(unit.getId(), LeaseStatus.ACTIVE)) {
            throw new BusinessValidationException(
                    "Impossible de créer un bail : l'appartement " + unit.getDoorNumber() + " a déjà un bail actif."
            );
        }

        User tenant = userRepository.findByEmail(request.getTenantEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.getTenantEmail()));

        Lease lease = Lease.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .baseRentAmount(request.getBaseRentAmount())
                .utilityAmount(request.getUtilityAmount())
                .status(LeaseStatus.ACTIVE)
                .tenant(tenant)
                .unit(unit)
                .build();

        leaseRepository.save(lease);

        return leaseMapper.toDto(lease);
    }

    public List<LeaseDTO> getLeasesForLandlord() {
        return leaseRepository.findByUnitBuildingLandlordEmail(getCurrentUserEmail())
                .stream()
                .map(leaseMapper::toDto)
                .toList();
    }

    /** Tâche 4 — GET /api/leases/my-lease */
    public LeaseDTO getMyActiveLease() {
        String email = getCurrentUserEmail();
        Lease lease = leaseRepository.findByTenantEmailAndStatus(email, LeaseStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Bail actif", email));
        return leaseMapper.toDto(lease);
    }

    /** Tâche 5 — DELETE /api/leases/{id} — résiliation logique */
    @Transactional
    public void terminateLease(Long id) {
        Lease lease = leaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", id));

        if (!lease.getUnit().getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : ce bail ne vous appartient pas.");
        }

        if (lease.getStatus() == LeaseStatus.TERMINATED) {
            throw new BusinessValidationException("Ce bail est déjà résilié.");
        }

        lease.setStatus(LeaseStatus.TERMINATED);
        leaseRepository.save(lease);
    }

    /** Tâche 6 — GET /api/leases/unit/{unitId} */
    public LeaseDTO getLeaseByUnit(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", unitId));

        if (!unit.getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : cet appartement ne vous appartient pas.");
        }

        Lease lease = leaseRepository.findByUnitIdAndStatus(unitId, LeaseStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Bail actif pour l'unité", unitId));
        return leaseMapper.toDto(lease);
    }
}

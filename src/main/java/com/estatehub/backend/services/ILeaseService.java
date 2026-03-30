package com.estatehub.backend.services;

import com.estatehub.backend.dtos.CreateLeaseRequest;
import com.estatehub.backend.dtos.LeaseDTO;

import java.util.List;

public interface ILeaseService {
    LeaseDTO createLease(CreateLeaseRequest request);
    List<LeaseDTO> getLeasesForLandlord();
    LeaseDTO getMyActiveLease();
    void terminateLease(Long id);
    LeaseDTO getLeaseByUnit(Long unitId);
}

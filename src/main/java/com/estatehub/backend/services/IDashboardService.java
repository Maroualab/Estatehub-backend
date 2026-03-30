package com.estatehub.backend.services;

import com.estatehub.backend.dtos.DashboardStatsDTO;

public interface IDashboardService {
    DashboardStatsDTO getStats();
}

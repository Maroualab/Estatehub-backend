package com.estatehub.backend.services;

import com.estatehub.backend.dtos.MonthlyRevenuePointDTO;
import com.estatehub.backend.dtos.StatsSummaryDTO;
import com.estatehub.backend.models.enums.LeaseStatus;
import com.estatehub.backend.repositories.InvoiceRepository;
import com.estatehub.backend.repositories.LeaseRepository;
import com.estatehub.backend.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StatsService implements IStatsService {

    private final UnitRepository unitRepository;
    private final LeaseRepository leaseRepository;
    private final InvoiceRepository invoiceRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public StatsSummaryDTO getSummary() {
        String email = getCurrentUserEmail();

        long totalUnits = unitRepository.countByBuildingLandlordEmail(email);
        long occupiedUnits = leaseRepository.countOccupiedUnitsByLandlordEmail(email, LeaseStatus.ACTIVE);

        double occupancyRate = totalUnits == 0
                ? 0.0
                : BigDecimal.valueOf((occupiedUnits * 100.0) / totalUnits)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        BigDecimal theoreticalRevenue = leaseRepository
                .sumTotalMonthlyPaymentByLandlordEmailAndStatus(email, LeaseStatus.ACTIVE);

        BigDecimal actualRevenue = invoiceRepository.sumPaidByLandlordEmail(email);

        List<MonthlyRevenuePointDTO> monthlySeries = buildLastSixMonthsSeries(email);
        double portfolioGrowth = calculateGrowth(monthlySeries);

        return StatsSummaryDTO.builder()
                .occupancyRate(occupancyRate)
                .theoreticalMonthlyRevenue(theoreticalRevenue)
                .actualMonthlyRevenue(actualRevenue)
                .portfolioGrowth(portfolioGrowth)
                .monthlyRevenueSeries(monthlySeries)
                .build();
    }

    private List<MonthlyRevenuePointDTO> buildLastSixMonthsSeries(String email) {
        List<MonthlyRevenuePointDTO> series = new ArrayList<>();
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = currentMonth.minusMonths(i);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            BigDecimal paidAmount = invoiceRepository
                    .sumPaidByLandlordEmailAndIssueDateBetween(email, monthStart, monthEnd);

            String monthLabel = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE);
            series.add(MonthlyRevenuePointDTO.builder()
                    .month(monthLabel)
                    .amount(paidAmount)
                    .build());
        }

        return series;
    }

    private double calculateGrowth(List<MonthlyRevenuePointDTO> series) {
        if (series.isEmpty()) {
            return 0.0;
        }

        BigDecimal first = series.get(0).getAmount();
        BigDecimal last = series.get(series.size() - 1).getAmount();

        if (first == null || first.signum() <= 0 || last == null) {
            return 0.0;
        }

        BigDecimal growth = last.subtract(first)
                .multiply(BigDecimal.valueOf(100))
                .divide(first, 1, RoundingMode.HALF_UP);

        return growth.doubleValue();
    }
}

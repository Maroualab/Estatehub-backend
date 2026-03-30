package com.estatehub.backend.repositories;

import com.estatehub.backend.models.Invoice;
import com.estatehub.backend.models.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByLeaseId(Long leaseId);
    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Toutes les factures des baux appartenant au bailleur donné,
     * triées par date d'émission décroissante.
     */
    @Query("SELECT i FROM Invoice i " +
           "WHERE i.lease.unit.building.landlord.email = :email " +
           "ORDER BY i.issueDate DESC")
    List<Invoice> findByLandlordEmail(@Param("email") String email);

        @Query("SELECT i FROM Invoice i " +
            "WHERE i.lease.tenant.email = :email AND i.status <> 'PAID' " +
            "ORDER BY i.dueDate ASC")
        List<Invoice> findUnpaidByTenantEmail(@Param("email") String email);

        @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
            "WHERE i.lease.unit.building.landlord.email = :email AND i.status = 'PAID'")
        BigDecimal sumPaidByLandlordEmail(@Param("email") String email);

        @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
            "WHERE i.lease.unit.building.landlord.email = :email " +
            "AND i.status = 'PAID' AND i.issueDate BETWEEN :start AND :end")
        BigDecimal sumPaidByLandlordEmailAndIssueDateBetween(
             @Param("email") String email,
             @Param("start") LocalDate start,
             @Param("end") LocalDate end);

    /**
     * Vérifie si une facture existe déjà pour ce bail sur la période donnée.
     * Utilisé pour éviter la génération en double (une facture par bail par mois).
     */
    boolean existsByLeaseIdAndIssueDateBetween(Long leaseId, LocalDate start, LocalDate end);
}

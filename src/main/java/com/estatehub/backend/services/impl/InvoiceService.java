package com.estatehub.backend.services.impl;

import com.estatehub.backend.dtos.InvoiceDTO;
import com.estatehub.backend.exceptions.BusinessValidationException;
import com.estatehub.backend.exceptions.ResourceNotFoundException;
import com.estatehub.backend.mappers.InvoiceMapper;
import com.estatehub.backend.models.Invoice;
import com.estatehub.backend.models.Lease;
import com.estatehub.backend.models.enums.InvoiceStatus;
import com.estatehub.backend.models.enums.LeaseStatus;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.estatehub.backend.repositories.InvoiceRepository;
import com.estatehub.backend.repositories.LeaseRepository;
import com.estatehub.backend.services.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final LeaseRepository   leaseRepository;
    private final InvoiceMapper     invoiceMapper;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** Vérifie la propriété du bail et le retourne. */
    private Lease getOwnedLease(Long leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease", leaseId));
        if (!lease.getUnit().getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé : ce bail ne vous appartient pas.");
        }
        return lease;
    }

    /** Vérifie la propriété de la facture pour le bailleur connecté. */
    private Invoice getOwnedInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
        if (!invoice.getLease().getUnit().getBuilding().getLandlord().getEmail().equals(getCurrentUserEmail())) {
            throw new BusinessValidationException("Accès refusé.");
        }
        return invoice;
    }

    /** Vérifie la propriété de la facture pour le bailleur OU le locataire connecté. */
    private Invoice getOwnedInvoiceForPayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));

        String currentEmail = getCurrentUserEmail();
        String landlordEmail = invoice.getLease().getUnit().getBuilding().getLandlord().getEmail();
        String tenantEmail = invoice.getLease().getTenant().getEmail();

        if (!currentEmail.equals(landlordEmail) && !currentEmail.equals(tenantEmail)) {
            throw new BusinessValidationException("Accès refusé.");
        }
        return invoice;
    }

    // ------------------------------------------------------------------ READ ALL (bailleur)
    public List<InvoiceDTO> getInvoicesForLandlord() {
        return invoiceRepository.findByLandlordEmail(getCurrentUserEmail())
                .stream().map(invoiceMapper::toDto).toList();
    }

    // ------------------------------------------------------------------ READ UNPAID (locataire)
    public List<InvoiceDTO> getUnpaidInvoicesForTenant() {
        return invoiceRepository.findUnpaidByTenantEmail(getCurrentUserEmail())
                .stream().map(invoiceMapper::toDto).toList();
    }

    // ------------------------------------------------------------------ READ BY LEASE
    public List<InvoiceDTO> getInvoicesForLease(Long leaseId) {
        getOwnedLease(leaseId); // ownership check
        return invoiceRepository.findByLeaseId(leaseId)
                .stream().map(invoiceMapper::toDto).toList();
    }

    // ------------------------------------------------------------------ GENERATE (single lease)
    /**
     * Tâche 6 — Génère la facture du mois courant pour un bail.
     * Règles :
     *  - Le bail doit être ACTIVE.
     *  - Une seule facture par bail par mois (idempotence).
     *  - totalAmount = baseRentAmount + utilityAmount
     *  - dueDate = issueDate + 30 jours
     */
    @Transactional
    public InvoiceDTO generateForLease(Long leaseId) {
        Lease lease = getOwnedLease(leaseId);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new BusinessValidationException(
                    "Impossible de facturer le bail #" + leaseId + " : statut " + lease.getStatus());
        }

        LocalDate today      = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd   = today.withDayOfMonth(today.lengthOfMonth());

        if (invoiceRepository.existsByLeaseIdAndIssueDateBetween(leaseId, monthStart, monthEnd)) {
            throw new BusinessValidationException(
                    "Une facture existe déjà pour le bail #" + leaseId + " ce mois-ci (" +
                    today.getMonth() + " " + today.getYear() + ").");
        }

        Invoice invoice = Invoice.builder()
                .lease(lease)
                .issueDate(today)
                .dueDate(today.plusDays(30))
                .totalAmount(lease.getTotalMonthlyPayment())
                .status(InvoiceStatus.SENT)
                .build();

        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    // ------------------------------------------------------------------ GENERATE ALL (mass)
    /**
     * Tâche 7 — Génère les factures du mois pour tous les baux ACTIFS du bailleur connecté.
     * Ignore silencieusement les baux qui ont déjà une facture ce mois-ci (idempotent).
     * Retourne la liste des factures effectivement créées.
     */
    @Transactional
    public List<InvoiceDTO> generateMonthlyForAll() {
        String email     = getCurrentUserEmail();
        LocalDate today  = LocalDate.now();
        LocalDate start  = today.withDayOfMonth(1);
        LocalDate end    = today.withDayOfMonth(today.lengthOfMonth());

        return leaseRepository.findByUnitBuildingLandlordEmail(email)
                .stream()
                .filter(l -> l.getStatus() == LeaseStatus.ACTIVE)
                .filter(l -> !invoiceRepository.existsByLeaseIdAndIssueDateBetween(l.getId(), start, end))
                .map(l -> {
                    Invoice inv = Invoice.builder()
                            .lease(l)
                            .issueDate(today)
                            .dueDate(today.plusDays(30))
                            .totalAmount(l.getTotalMonthlyPayment())
                            .status(InvoiceStatus.SENT)
                            .build();
                    return invoiceMapper.toDto(invoiceRepository.save(inv));
                })
                .toList();
    }

    // ------------------------------------------------------------------ GENERATE ALL (scheduler)
    @Transactional
    public int generateMonthlyForAllLandlords() {
        LocalDate today  = LocalDate.now();
        LocalDate start  = today.withDayOfMonth(1);
        LocalDate end    = today.withDayOfMonth(today.lengthOfMonth());

        int created = 0;
        for (Lease lease : leaseRepository.findByStatus(LeaseStatus.ACTIVE)) {
            if (invoiceRepository.existsByLeaseIdAndIssueDateBetween(lease.getId(), start, end)) {
                continue;
            }

            Invoice inv = Invoice.builder()
                    .lease(lease)
                    .issueDate(today)
                    .dueDate(today.plusDays(30))
                    .totalAmount(lease.getTotalMonthlyPayment())
                    .status(InvoiceStatus.SENT)
                    .build();
            invoiceRepository.save(inv);
            created++;
        }
        return created;
    }

    // ------------------------------------------------------------------ MARK AS PAID
    @Transactional
    public InvoiceDTO markAsPaid(Long id) {
        Invoice invoice = getOwnedInvoiceForPayment(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessValidationException("Cette facture est déjà marquée comme payée.");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    // ------------------------------------------------------------------ PDF EXPORT
    public byte[] buildInvoicePdf(Long id) {
        Invoice invoice = getOwnedInvoice(id);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();

            document.add(new Paragraph("EstateHub - Invoice", new Font(Font.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Invoice ID: " + invoice.getId()));
            document.add(new Paragraph("Issue date: " + invoice.getIssueDate()));
            document.add(new Paragraph("Due date: " + invoice.getDueDate()));
            document.add(new Paragraph("Status: " + invoice.getStatus().name()));
            document.add(new Paragraph("Tenant: " + invoice.getLease().getTenant().getEmail()));
            document.add(new Paragraph("Building: " + invoice.getLease().getUnit().getBuilding().getName()));
            document.add(new Paragraph("Amount: " + invoice.getTotalAmount()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated by EstateHub."));

            document.close();
            return output.toByteArray();
        } catch (DocumentException ex) {
            throw new BusinessValidationException("Impossible de générer le PDF de la facture.");
        }
    }
}

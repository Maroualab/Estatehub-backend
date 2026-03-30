package com.estatehub.backend.controllers;

import com.estatehub.backend.dtos.InvoiceDTO;
import com.estatehub.backend.services.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesForLandlord() {
        return ResponseEntity.ok(invoiceService.getInvoicesForLandlord());
    }

    @GetMapping("/my-unpaid")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<InvoiceDTO>> getUnpaidInvoicesForTenant() {
        return ResponseEntity.ok(invoiceService.getUnpaidInvoicesForTenant());
    }

    @GetMapping("/lease/{leaseId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesForLease(@PathVariable Long leaseId) {
        return ResponseEntity.ok(invoiceService.getInvoicesForLease(leaseId));
    }

    @PostMapping("/generate/{leaseId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<InvoiceDTO> generateForLease(@PathVariable Long leaseId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.generateForLease(leaseId));
    }

    @PostMapping("/generate-monthly")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<InvoiceDTO>> generateMonthlyForAll() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.generateMonthlyForAll());
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('TENANT')")
    public ResponseEntity<InvoiceDTO> pay(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @PatchMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('TENANT')")
    public ResponseEntity<InvoiceDTO> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.buildInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

package com.estatehub.backend.services;

import com.estatehub.backend.dtos.InvoiceDTO;

import java.util.List;

public interface IInvoiceService {
    List<InvoiceDTO> getInvoicesForLandlord();
    List<InvoiceDTO> getUnpaidInvoicesForTenant();
    List<InvoiceDTO> getInvoicesForLease(Long leaseId);
    InvoiceDTO generateForLease(Long leaseId);
    List<InvoiceDTO> generateMonthlyForAll();
    int generateMonthlyForAllLandlords();
    InvoiceDTO markAsPaid(Long id);
    byte[] buildInvoicePdf(Long id);
}

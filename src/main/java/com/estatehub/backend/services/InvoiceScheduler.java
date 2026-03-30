package com.estatehub.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final IInvoiceService invoiceService;

    /**
     * Scheduled task: Generates monthly invoices for all landlords.
     * Runs at 00:00 on the 1st of each month (UTC).
     * 
     * Monitoring:
     * - Memory usage before/after generation
     * - Execution duration
     * - Invoice batch sizes
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlyInvoices() {
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        log.info("═══════════════════════════════════════════════════════════════");
        log.info("[SCHEDULER START] Monthly Invoice Generation");
        log.info("Memory before: {} MB", formatMemory(memoryBefore));
        
        try {
            int created = invoiceService.generateMonthlyForAllLandlords();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryDelta = memoryAfter - memoryBefore;
            
            log.info("[SCHEDULER SUCCESS] {} invoices generated", created);
            log.info("Memory after: {} MB (delta: {} MB)", formatMemory(memoryAfter), formatMemory(memoryDelta));
            log.info("Duration: {} ms ({} sec)", duration, duration / 1000);
            log.info("═══════════════════════════════════════════════════════════════");
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.error("[SCHEDULER FAILED] Exception after {} ms", duration, e);
            throw new RuntimeException("Invoice generation failed", e);
        }
    }

    /**
     * Formats bytes to MB for readable logging.
     */
    private String formatMemory(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0));
    }
}

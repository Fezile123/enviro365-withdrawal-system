package com.enviro.assessment.junior.fezile.controller;

import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.enviro.assessment.junior.fezile.service.CsvExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Satisfies the mandatory "Export CSV statements with filtering" backend
 * requirement, and backs the "CSV download button" frontend requirement.
 *
 * All filter parameters are optional query params:
 *   GET /api/investors/1/withdrawals/export
 *   GET /api/investors/1/withdrawals/export?status=APPROVED
 *   GET /api/investors/1/withdrawals/export?from=2026-01-01T00:00:00&to=2026-12-31T23:59:59
 */
@RestController
@RequestMapping("/api/investors/{investorId}/withdrawals/export")
public class CsvExportController {

    private final CsvExportService csvExportService;

    public CsvExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public ResponseEntity<byte[]> exportCsv(
            @PathVariable Long investorId,
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        byte[] csv = csvExportService.exportWithdrawalsAsCsv(investorId, status, from, to);

        String filename = "withdrawals_investor_" + investorId + ".csv";
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}

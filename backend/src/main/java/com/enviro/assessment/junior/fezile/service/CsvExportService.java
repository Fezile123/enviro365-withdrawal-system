package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Satisfies the mandatory "Export CSV statements with filtering" backend
 * requirement. Filtering is optional: callers can narrow by status, by a
 * date range, or both; with no filters supplied, the full withdrawal
 * history is exported.
 *
 * Written by hand (no external CSV library) since the fields involved
 * (amounts, dates, product names) are simple enough not to need one, and
 * it keeps the dependency list minimal.
 */
@Service
public class CsvExportService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InvestorRepository investorRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public CsvExportService(InvestorRepository investorRepository,
                             WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.investorRepository = investorRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    /**
     * Builds a CSV statement of withdrawal notices for an investor.
     *
     * @param investorId the investor to export for
     * @param status     optional status filter (e.g. only APPROVED)
     * @param from       optional inclusive lower bound on createdAt
     * @param to         optional inclusive upper bound on createdAt
     */
    public byte[] exportWithdrawalsAsCsv(Long investorId,
                                          WithdrawalStatus status,
                                          LocalDateTime from,
                                          LocalDateTime to) {
        if (!investorRepository.existsById(investorId)) {
            throw new ResourceNotFoundException("Investor not found with id: " + investorId);
        }

        List<WithdrawalNotice> notices = resolveNotices(investorId, status, from, to);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(outputStream, true, StandardCharsets.UTF_8)) {
            writer.println("Notice ID,Product,Amount,Balance After Withdrawal,Status,Date");
            for (WithdrawalNotice notice : notices) {
                writer.println(toCsvRow(notice));
            }
        }
        return outputStream.toByteArray();
    }

    private List<WithdrawalNotice> resolveNotices(Long investorId,
                                                    WithdrawalStatus status,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
        if (from != null && to != null) {
            List<WithdrawalNotice> byDate = withdrawalNoticeRepository
                    .findByInvestorIdAndCreatedAtBetween(investorId, from, to);
            return status == null ? byDate : byDate.stream()
                    .filter(n -> n.getStatus() == status)
                    .toList();
        }
        if (status != null) {
            return withdrawalNoticeRepository.findByInvestorIdAndStatus(investorId, status);
        }
        return withdrawalNoticeRepository.findByInvestorId(investorId);
    }

    private String toCsvRow(WithdrawalNotice notice) {
        return String.join(",",
                String.valueOf(notice.getId()),
                escape(notice.getProduct().getName()),
                notice.getAmount().toPlainString(),
                notice.getBalanceAfterWithdrawal().toPlainString(),
                notice.getStatus().name(),
                notice.getCreatedAt().format(TIMESTAMP_FORMAT)
        );
    }

    /** Wraps a field in quotes if it contains a comma, so the CSV stays valid. */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.contains(",") ? "\"" + value + "\"" : value;
    }
}

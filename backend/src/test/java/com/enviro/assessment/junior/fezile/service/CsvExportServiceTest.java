package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Product;
import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvExportServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    private CsvExportService csvExportService;

    private WithdrawalNotice notice;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService(investorRepository, withdrawalNoticeRepository);

        Product product = new Product();
        product.setName("Enviro365 Retirement Annuity");

        notice = new WithdrawalNotice();
        notice.setId(1L);
        notice.setProduct(product);
        notice.setAmount(new BigDecimal("10000.00"));
        notice.setBalanceAfterWithdrawal(new BigDecimal("490000.00"));
        notice.setStatus(WithdrawalStatus.APPROVED);
        notice.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 30, 0));
    }

    @Test
    void exportWithdrawalsAsCsv_investorNotFound_throwsResourceNotFoundException() {
        when(investorRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> csvExportService.exportWithdrawalsAsCsv(99L, null, null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void exportWithdrawalsAsCsv_noFilters_includesHeaderAndAllRows() {
        when(investorRepository.existsById(1L)).thenReturn(true);
        when(withdrawalNoticeRepository.findByInvestorId(1L)).thenReturn(List.of(notice));

        String csv = new String(csvExportService.exportWithdrawalsAsCsv(1L, null, null, null), StandardCharsets.UTF_8);

        assertThat(csv).contains("Notice ID,Product,Amount,Balance After Withdrawal,Status,Date");
        assertThat(csv).contains("1,Enviro365 Retirement Annuity,10000.00,490000.00,APPROVED,2026-01-15 10:30:00");
    }

    @Test
    void exportWithdrawalsAsCsv_statusFilter_delegatesToFilteredRepositoryQuery() {
        when(investorRepository.existsById(1L)).thenReturn(true);
        when(withdrawalNoticeRepository.findByInvestorIdAndStatus(1L, WithdrawalStatus.APPROVED))
                .thenReturn(List.of(notice));

        csvExportService.exportWithdrawalsAsCsv(1L, WithdrawalStatus.APPROVED, null, null);

        verify(withdrawalNoticeRepository).findByInvestorIdAndStatus(1L, WithdrawalStatus.APPROVED);
    }

    @Test
    void exportWithdrawalsAsCsv_noMatchingNotices_returnsHeaderOnly() {
        when(investorRepository.existsById(1L)).thenReturn(true);
        when(withdrawalNoticeRepository.findByInvestorId(1L)).thenReturn(List.of());

        String csv = new String(csvExportService.exportWithdrawalsAsCsv(1L, null, null, null), StandardCharsets.UTF_8);
        String[] lines = csv.strip().split("\\R");

        assertThat(lines).hasSize(1); // header only
    }

    @Test
    void exportWithdrawalsAsCsv_fromOnly_stillAppliesDateFilter() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        when(investorRepository.existsById(1L)).thenReturn(true);
        // "to" should be defaulted to "now" rather than the filter being skipped entirely
        when(withdrawalNoticeRepository.findByInvestorIdAndCreatedAtBetween(
                eq(1L), eq(from), any(LocalDateTime.class)))
                .thenReturn(List.of(notice));

        byte[] result = csvExportService.exportWithdrawalsAsCsv(1L, null, from, null);

        assertThat(new String(result, StandardCharsets.UTF_8)).contains("Enviro365 Retirement Annuity");
        verify(withdrawalNoticeRepository).findByInvestorIdAndCreatedAtBetween(eq(1L), eq(from), any());
    }

    @Test
    void exportWithdrawalsAsCsv_toOnly_stillAppliesDateFilter() {
        LocalDateTime to = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        when(investorRepository.existsById(1L)).thenReturn(true);
        when(withdrawalNoticeRepository.findByInvestorIdAndCreatedAtBetween(
                eq(1L), any(LocalDateTime.class), eq(to)))
                .thenReturn(List.of(notice));

        csvExportService.exportWithdrawalsAsCsv(1L, null, null, to);

        verify(withdrawalNoticeRepository).findByInvestorIdAndCreatedAtBetween(eq(1L), any(), eq(to));
    }
}

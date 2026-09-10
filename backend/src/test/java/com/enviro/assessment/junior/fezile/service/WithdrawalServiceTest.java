package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.dto.WithdrawalRequest;
import com.enviro.assessment.junior.fezile.exception.BusinessRuleViolationException;
import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.model.Product;
import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalType;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.ProductRepository;
import com.enviro.assessment.junior.fezile.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the three withdrawal business rules, plus the supporting
 * lookups (investor/product not found, product belonging to a different
 * investor). Repositories are mocked so these run fast and in isolation --
 * no Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor overSixtyFiveInvestor;
    private Investor underSixtyFiveInvestor;
    private Product retirementProduct;
    private Product voluntaryProduct;

    @BeforeEach
    void setUp() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);

        overSixtyFiveInvestor = new Investor();
        overSixtyFiveInvestor.setId(1L);
        overSixtyFiveInvestor.setDateOfBirth(LocalDate.now().minusYears(71));
        overSixtyFiveInvestor.setPortfolio(portfolio);
        portfolio.setInvestor(overSixtyFiveInvestor);

        underSixtyFiveInvestor = new Investor();
        underSixtyFiveInvestor.setId(2L);
        underSixtyFiveInvestor.setDateOfBirth(LocalDate.now().minusYears(40));

        retirementProduct = new Product();
        retirementProduct.setId(10L);
        retirementProduct.setType(WithdrawalType.RETIREMENT);
        retirementProduct.setBalance(new BigDecimal("500000.00"));
        retirementProduct.setPortfolio(portfolio);

        voluntaryProduct = new Product();
        voluntaryProduct.setId(11L);
        voluntaryProduct.setType(WithdrawalType.VOLUNTARY);
        voluntaryProduct.setBalance(new BigDecimal("150000.00"));
        voluntaryProduct.setPortfolio(portfolio);
    }

    private WithdrawalRequest request(Long productId, String amount) {
        WithdrawalRequest req = new WithdrawalRequest();
        req.setProductId(productId);
        req.setAmount(new BigDecimal(amount));
        return req;
    }

    @Test
    void successfulWithdrawal_reducesBalanceAndSavesNotice() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalNotice result = withdrawalService.createWithdrawal(1L, request(10L, "10000.00"));

        assertThat(result.getAmount()).isEqualByComparingTo("10000.00");
        assertThat(result.getBalanceAfterWithdrawal()).isEqualByComparingTo("490000.00");
        assertThat(result.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
        assertThat(retirementProduct.getBalance()).isEqualByComparingTo("490000.00");
        verify(productRepository).save(retirementProduct);
    }

    @Test
    void retirementWithdrawal_rejectedWhenInvestorNotOverSixtyFive() {
        when(investorRepository.findById(2L)).thenReturn(Optional.of(underSixtyFiveInvestor));
        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(underSixtyFiveInvestor);
        retirementProduct.setPortfolio(portfolio);
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(2L, request(10L, "1000.00")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("over the age of 65");

        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    void retirementWithdrawal_allowedWhenInvestorOverSixtyFive() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalNotice result = withdrawalService.createWithdrawal(1L, request(10L, "5000.00"));

        assertThat(result.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
    }

    @Test
    void voluntaryWithdrawal_notSubjectToAgeRule() {
        when(investorRepository.findById(2L)).thenReturn(Optional.of(underSixtyFiveInvestor));
        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(underSixtyFiveInvestor);
        voluntaryProduct.setPortfolio(portfolio);
        when(productRepository.findById(11L)).thenReturn(Optional.of(voluntaryProduct));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalNotice result = withdrawalService.createWithdrawal(2L, request(11L, "1000.00"));

        assertThat(result.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(1L, request(10L, "600000.00")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("exceeds available balance");

        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsNinetyPercentOfBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));

        // 90% of 500,000 is 450,000 -- 460,000 should be rejected
        assertThatThrownBy(() -> withdrawalService.createWithdrawal(1L, request(10L, "460000.00")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("90%");

        verify(withdrawalNoticeRepository, never()).save(any());
    }

    @Test
    void withdrawal_allowedAtExactlyNinetyPercentOfBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // exactly 90% of 500,000 is 450,000 -- should be allowed (not exceeding)
        WithdrawalNotice result = withdrawalService.createWithdrawal(1L, request(10L, "450000.00"));

        assertThat(result.getStatus()).isEqualTo(WithdrawalStatus.APPROVED);
    }

    @Test
    void createWithdrawal_throwsWhenInvestorNotFound() {
        when(investorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(99L, request(10L, "100.00")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createWithdrawal_throwsWhenProductNotFound() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(1L, request(999L, "100.00")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createWithdrawal_rejectedWhenProductBelongsToAnotherInvestor() {
        Investor otherInvestor = new Investor();
        otherInvestor.setId(2L);
        Portfolio otherPortfolio = new Portfolio();
        otherPortfolio.setInvestor(otherInvestor);
        retirementProduct.setPortfolio(otherPortfolio);

        when(investorRepository.findById(1L)).thenReturn(Optional.of(overSixtyFiveInvestor));
        when(productRepository.findById(10L)).thenReturn(Optional.of(retirementProduct));

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(1L, request(10L, "100.00")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("does not belong to investor");
    }
}

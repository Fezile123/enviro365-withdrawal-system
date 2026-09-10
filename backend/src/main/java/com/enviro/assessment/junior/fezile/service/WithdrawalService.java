package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.dto.WithdrawalRequest;
import com.enviro.assessment.junior.fezile.exception.BusinessRuleViolationException;
import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.model.Product;
import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalType;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.ProductRepository;
import com.enviro.assessment.junior.fezile.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implements the three withdrawal business rules from the assessment brief:
 *
 *   1. Retirement withdrawals only allowed if age > 65
 *   2. Withdrawal must not exceed balance
 *   3. Withdrawal must not exceed 90% of balance
 *
 * Rules 2 and 3 apply to every withdrawal; rule 1 only applies when the
 * product being withdrawn from is a RETIREMENT product.
 */
@Service
public class WithdrawalService {

    /** Rule 3: a single withdrawal can take at most this fraction of the balance. */
    private static final BigDecimal MAX_WITHDRAWAL_FRACTION = new BigDecimal("0.90");

    /** Rule 1: minimum age for a retirement withdrawal. Must be strictly greater than this. */
    private static final int MIN_RETIREMENT_AGE = 65;

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalService(InvestorRepository investorRepository,
                              ProductRepository productRepository,
                              WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.investorRepository = investorRepository;
        this.productRepository = productRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    @Transactional
    public WithdrawalNotice createWithdrawal(Long investorId, WithdrawalRequest request) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (!product.getPortfolio().getInvestor().getId().equals(investorId)) {
            throw new BusinessRuleViolationException(
                    "Product " + product.getId() + " does not belong to investor " + investorId);
        }

        validateBusinessRules(investor, product, request.getAmount());

        BigDecimal newBalance = product.getBalance().subtract(request.getAmount());
        product.setBalance(newBalance);
        productRepository.save(product);

        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setInvestor(investor);
        notice.setProduct(product);
        notice.setAmount(request.getAmount());
        notice.setBalanceAfterWithdrawal(newBalance);
        notice.setStatus(WithdrawalStatus.APPROVED);
        notice.setCreatedAt(LocalDateTime.now());

        return withdrawalNoticeRepository.save(notice);
    }

    public List<WithdrawalNotice> getHistory(Long investorId) {
        if (!investorRepository.existsById(investorId)) {
            throw new ResourceNotFoundException("Investor not found with id: " + investorId);
        }
        return withdrawalNoticeRepository.findByInvestorId(investorId);
    }

    private void validateBusinessRules(Investor investor, Product product, BigDecimal amount) {
        // Rule 1: retirement withdrawals only allowed if age > 65
        if (product.getType() == WithdrawalType.RETIREMENT && investor.getAge() <= MIN_RETIREMENT_AGE) {
            throw new BusinessRuleViolationException(
                    "Retirement withdrawals are only allowed for investors over the age of "
                            + MIN_RETIREMENT_AGE + ". Investor is currently " + investor.getAge() + ".");
        }

        // Rule 2: withdrawal must not exceed balance
        if (amount.compareTo(product.getBalance()) > 0) {
            throw new BusinessRuleViolationException(
                    "Withdrawal amount (" + amount + ") exceeds available balance (" + product.getBalance() + ").");
        }

        // Rule 3: withdrawal must not exceed 90% of balance
        BigDecimal maxAllowed = product.getBalance().multiply(MAX_WITHDRAWAL_FRACTION);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new BusinessRuleViolationException(
                    "Withdrawal amount (" + amount + ") exceeds the maximum allowed withdrawal of 90% of balance ("
                            + maxAllowed + ").");
        }
    }
}

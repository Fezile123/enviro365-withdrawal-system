package com.enviro.assessment.junior.fezile.config;

import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.model.Product;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalType;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.PortfolioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the in-memory H2 database with sample data on every startup so the
 * API has something meaningful to serve without any manual setup.
 *
 * Includes one investor over 65 (so the retirement withdrawal age rule can
 * actually be exercised) with one RETIREMENT product and one VOLUNTARY
 * product.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;

    public DataSeeder(InvestorRepository investorRepository, PortfolioRepository portfolioRepository) {
        this.investorRepository = investorRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public void run(String... args) {
        Investor investor = new Investor();
        investor.setFirstName("Thandiwe");
        investor.setLastName("Nkosi");
        investor.setDateOfBirth(LocalDate.of(1955, 5, 10)); // over 65 as of 2026
        investor.setEmail("thandiwe.nkosi@example.com");
        investorRepository.save(investor);

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);

        Product retirementAnnuity = new Product();
        retirementAnnuity.setName("Enviro365 Retirement Annuity");
        retirementAnnuity.setType(WithdrawalType.RETIREMENT);
        retirementAnnuity.setBalance(new BigDecimal("500000.00"));

        Product unitTrust = new Product();
        unitTrust.setName("Enviro365 Unit Trust");
        unitTrust.setType(WithdrawalType.VOLUNTARY);
        unitTrust.setBalance(new BigDecimal("150000.00"));

        portfolio.addProduct(retirementAnnuity);
        portfolio.addProduct(unitTrust);

        investor.setPortfolio(portfolio);
        portfolioRepository.save(portfolio);
    }
}

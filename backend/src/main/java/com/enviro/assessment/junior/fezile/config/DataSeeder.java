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
 * Seeds two investors deliberately on opposite sides of the retirement age
 * rule, so both paths of that business rule can be demoed/tested through
 * the actual UI without editing data by hand:
 *   - Thandiwe Nkosi (71) -- retirement withdrawals are allowed
 *   - Sipho Dlamini (40) -- retirement withdrawals should be rejected
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
        seedInvestor(
                "Thandiwe", "Nkosi", LocalDate.of(1955, 5, 10), "thandiwe.nkosi@example.com",
                "Enviro365 Retirement Annuity", new BigDecimal("500000.00"),
                "Enviro365 Unit Trust", new BigDecimal("150000.00")
        );

        seedInvestor(
                "Sipho", "Dlamini", LocalDate.of(1986, 3, 22), "sipho.dlamini@example.com",
                "Enviro365 Retirement Annuity", new BigDecimal("220000.00"),
                "Enviro365 Unit Trust", new BigDecimal("80000.00")
        );
    }

    private void seedInvestor(String firstName, String lastName, LocalDate dateOfBirth, String email,
                               String retirementProductName, BigDecimal retirementBalance,
                               String voluntaryProductName, BigDecimal voluntaryBalance) {
        Investor investor = new Investor();
        investor.setFirstName(firstName);
        investor.setLastName(lastName);
        investor.setDateOfBirth(dateOfBirth);
        investor.setEmail(email);
        investorRepository.save(investor);

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);

        Product retirementAnnuity = new Product();
        retirementAnnuity.setName(retirementProductName);
        retirementAnnuity.setType(WithdrawalType.RETIREMENT);
        retirementAnnuity.setBalance(retirementBalance);

        Product unitTrust = new Product();
        unitTrust.setName(voluntaryProductName);
        unitTrust.setType(WithdrawalType.VOLUNTARY);
        unitTrust.setBalance(voluntaryBalance);

        portfolio.addProduct(retirementAnnuity);
        portfolio.addProduct(unitTrust);

        investor.setPortfolio(portfolio);
        portfolioRepository.save(portfolio);
    }
}

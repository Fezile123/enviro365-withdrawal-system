package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.dto.RegisterRequest;
import com.enviro.assessment.junior.fezile.exception.BusinessRuleViolationException;
import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

/**
 * Deliberately simple: "login" is just an email lookup (no password), and
 * "register" creates a new investor with an empty portfolio. There's no
 * session/token issued -- the frontend just remembers which investor is
 * "logged in" for the duration of the page. This is NOT real
 * authentication; it's a convenience layer so the app can be demoed as a
 * specific investor. Real auth (Spring Security, password hashing,
 * sessions/JWTs) was intentionally scoped out since it isn't part of the
 * assessment brief.
 */
@Service
public class AuthService {

    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;

    public AuthService(InvestorRepository investorRepository, PortfolioRepository portfolioRepository) {
        this.investorRepository = investorRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Investor login(String email) {
        return investorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No investor found with email: " + email + ". Please register."));
    }

    public Investor register(RegisterRequest request) {
        if (investorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException(
                    "An investor with email " + request.getEmail() + " already exists.");
        }

        Investor investor = new Investor();
        investor.setFirstName(request.getFirstName());
        investor.setLastName(request.getLastName());
        investor.setDateOfBirth(request.getDateOfBirth());
        investor.setEmail(request.getEmail());
        investorRepository.save(investor);

        // New investors start with an empty portfolio -- no products until
        // Enviro365 sets some up for them. The dashboard handles an empty
        // product list gracefully.
        Portfolio portfolio = new Portfolio();
        portfolio.setInvestor(investor);
        investor.setPortfolio(portfolio);
        portfolioRepository.save(portfolio);

        return investor;
    }
}

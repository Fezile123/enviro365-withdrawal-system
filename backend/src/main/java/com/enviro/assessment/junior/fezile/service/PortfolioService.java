package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * Retrieves the portfolio (details + products) for a given investor.
     * This backs the mandatory "Retrieve investor portfolio" backend
     * requirement.
     */
    public Portfolio getPortfolioByInvestorId(Long investorId) {
        return portfolioRepository.findByInvestorId(investorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No portfolio found for investor id: " + investorId));
    }
}

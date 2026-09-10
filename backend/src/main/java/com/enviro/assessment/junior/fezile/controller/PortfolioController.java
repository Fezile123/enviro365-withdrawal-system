package com.enviro.assessment.junior.fezile.controller;

import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Satisfies the mandatory backend requirement:
 * "Retrieve investor portfolio (details + products)".
 */
@RestController
@RequestMapping("/api/investors/{investorId}/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public Portfolio getPortfolio(@PathVariable Long investorId) {
        return portfolioService.getPortfolioByInvestorId(investorId);
    }
}

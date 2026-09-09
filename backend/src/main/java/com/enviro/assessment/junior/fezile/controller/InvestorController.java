package com.enviro.assessment.junior.fezile.controller;

import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.service.InvestorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only investor lookup endpoints. In this assessment there's no
 * authentication/login flow, so listing investors is what lets the frontend
 * (and you, during testing) discover a valid investor id to work with.
 */
@RestController
@RequestMapping("/api/investors")
public class InvestorController {

    private final InvestorService investorService;

    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public List<Investor> getAllInvestors() {
        return investorService.getAllInvestors();
    }

    @GetMapping("/{id}")
    public Investor getInvestor(@PathVariable Long id) {
        return investorService.getInvestorById(id);
    }
}

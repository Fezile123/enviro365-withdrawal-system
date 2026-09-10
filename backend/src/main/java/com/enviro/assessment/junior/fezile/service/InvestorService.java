package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestorService {

    private final InvestorRepository investorRepository;

    public InvestorService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    public List<Investor> getAllInvestors() {
        return investorRepository.findAll();
    }

    public Investor getInvestorById(Long id) {
        return investorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + id));
    }
}

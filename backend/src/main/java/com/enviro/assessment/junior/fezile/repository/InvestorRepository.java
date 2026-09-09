package com.enviro.assessment.junior.fezile.repository;

import com.enviro.assessment.junior.fezile.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
}

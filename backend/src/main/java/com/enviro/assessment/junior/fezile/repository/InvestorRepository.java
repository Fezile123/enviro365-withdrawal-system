package com.enviro.assessment.junior.fezile.repository;

import com.enviro.assessment.junior.fezile.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorRepository extends JpaRepository<Investor, Long> {

    Optional<Investor> findByEmail(String email);

    boolean existsByEmail(String email);
}

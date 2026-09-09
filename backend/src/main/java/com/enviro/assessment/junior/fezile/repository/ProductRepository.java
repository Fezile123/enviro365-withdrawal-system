package com.enviro.assessment.junior.fezile.repository;

import com.enviro.assessment.junior.fezile.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByPortfolioId(Long portfolioId);
}

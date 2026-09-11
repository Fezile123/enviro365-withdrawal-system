package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Portfolio;
import com.enviro.assessment.junior.fezile.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    private PortfolioService portfolioService;

    @Test
    void getPortfolioByInvestorId_found_returnsIt() {
        portfolioService = new PortfolioService(portfolioRepository);
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        when(portfolioRepository.findByInvestorId(1L)).thenReturn(Optional.of(portfolio));

        Portfolio result = portfolioService.getPortfolioByInvestorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getPortfolioByInvestorId_notFound_throwsResourceNotFoundException() {
        portfolioService = new PortfolioService(portfolioRepository);
        when(portfolioRepository.findByInvestorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolioByInvestorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}

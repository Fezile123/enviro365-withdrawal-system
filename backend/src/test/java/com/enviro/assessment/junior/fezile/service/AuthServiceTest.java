package com.enviro.assessment.junior.fezile.service;

import com.enviro.assessment.junior.fezile.dto.RegisterRequest;
import com.enviro.assessment.junior.fezile.exception.BusinessRuleViolationException;
import com.enviro.assessment.junior.fezile.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.fezile.model.Investor;
import com.enviro.assessment.junior.fezile.repository.InvestorRepository;
import com.enviro.assessment.junior.fezile.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_existingEmail_returnsInvestor() {
        Investor investor = new Investor();
        investor.setId(1L);
        investor.setEmail("thandiwe.nkosi@example.com");
        when(investorRepository.findByEmail("thandiwe.nkosi@example.com")).thenReturn(Optional.of(investor));

        Investor result = authService.login("thandiwe.nkosi@example.com");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void login_unknownEmail_throwsResourceNotFoundException() {
        when(investorRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nobody@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Please register");
    }

    @Test
    void register_newEmail_createsInvestorWithEmptyPortfolio() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("New");
        request.setLastName("Investor");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setEmail("new.investor@example.com");

        when(investorRepository.existsByEmail("new.investor@example.com")).thenReturn(false);

        Investor result = authService.register(request);

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getPortfolio()).isNotNull();
        assertThat(result.getPortfolio().getProducts()).isEmpty();
        verify(investorRepository).save(result);
        verify(portfolioRepository).save(any());
    }

    @Test
    void register_duplicateEmail_throwsBusinessRuleViolationException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("thandiwe.nkosi@example.com");
        when(investorRepository.existsByEmail("thandiwe.nkosi@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("already exists");
    }
}

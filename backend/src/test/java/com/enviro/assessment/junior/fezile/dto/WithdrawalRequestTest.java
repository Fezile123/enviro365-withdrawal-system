package com.enviro.assessment.junior.fezile.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the bean-validation annotations on WithdrawalRequest directly
 * (via jakarta.validation's Validator), independent of Spring MVC --
 * confirms the "Input validation" advanced requirement's rules actually
 * fire as intended.
 */
class WithdrawalRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void validRequest_hasNoViolations() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("100.00"));

        Set<ConstraintViolation<WithdrawalRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void missingProductId_isRejected() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setProductId(null);
        request.setAmount(new BigDecimal("100.00"));

        Set<ConstraintViolation<WithdrawalRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("productId"));
    }

    @Test
    void missingAmount_isRejected() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setProductId(1L);
        request.setAmount(null);

        Set<ConstraintViolation<WithdrawalRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void zeroAmount_isRejected() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setProductId(1L);
        request.setAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<WithdrawalRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void negativeAmount_isRejected() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setProductId(1L);
        request.setAmount(new BigDecimal("-50.00"));

        Set<ConstraintViolation<WithdrawalRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }
}

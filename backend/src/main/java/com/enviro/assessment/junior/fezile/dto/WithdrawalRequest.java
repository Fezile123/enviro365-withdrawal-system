package com.enviro.assessment.junior.fezile.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request body for POST /api/investors/{investorId}/withdrawals.
 *
 * This is where the "Input validation" advanced requirement is satisfied:
 * bean-validation annotations here (enforced via @Valid in the controller)
 * catch malformed requests (missing product, zero/negative amount) with a
 * 400 + field-level error messages, before the request ever reaches the
 * business-rule checks in WithdrawalService.
 */
@Getter
@Setter
public class WithdrawalRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;
}

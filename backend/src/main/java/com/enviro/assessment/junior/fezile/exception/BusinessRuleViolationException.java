package com.enviro.assessment.junior.fezile.exception;

/**
 * Thrown when a withdrawal request fails one of the business rules
 * (retirement age, balance sufficiency, 90% cap). Translated to a 400
 * response with a clear message so the frontend can show the investor
 * exactly why the withdrawal was rejected.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}

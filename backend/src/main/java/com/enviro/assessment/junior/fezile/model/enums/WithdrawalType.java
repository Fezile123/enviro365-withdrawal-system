package com.enviro.assessment.junior.fezile.model.enums;

/**
 * The type of withdrawal being requested against an investment product.
 *
 * RETIREMENT withdrawals carry an extra business rule: they are only
 * permitted if the investor is older than 65 (see business rules in the
 * assessment brief).
 */
public enum WithdrawalType {
    RETIREMENT,
    VOLUNTARY
}

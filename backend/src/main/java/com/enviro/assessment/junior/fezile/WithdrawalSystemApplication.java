package com.enviro.assessment.junior.fezile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Enviro365 Investments Withdrawal Notice System.
 *
 * Scenario: investors can view their portfolios, submit withdrawal notices
 * (subject to business rules), view withdrawal history, and export CSV
 * statements.
 */
@SpringBootApplication
public class WithdrawalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithdrawalSystemApplication.class, args);
    }
}

package com.enviro.assessment.junior.fezile.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers Investor.getAge() directly, since the retirement withdrawal rule
 * depends entirely on it being correct -- including the edge case of
 * someone whose birthday hasn't happened yet this year (should NOT be
 * counted as a year older until the birthday actually passes).
 */
class InvestorTest {

    @Test
    void getAge_returnsCorrectAge_forPastBirthdayThisYear() {
        Investor investor = new Investor();
        investor.setDateOfBirth(LocalDate.now().minusYears(70).minusDays(1));

        assertThat(investor.getAge()).isEqualTo(70);
    }

    @Test
    void getAge_doesNotCountBirthdayYear_ifBirthdayHasNotOccurredYetThisYear() {
        Investor investor = new Investor();
        // Born exactly 70 years ago, but one day in the future relative to
        // "today" -- i.e. their 70th birthday hasn't happened yet this year.
        investor.setDateOfBirth(LocalDate.now().minusYears(70).plusDays(1));

        assertThat(investor.getAge()).isEqualTo(69);
    }

    @Test
    void getAge_returnsZero_whenDateOfBirthIsNull() {
        Investor investor = new Investor();
        investor.setDateOfBirth(null);

        assertThat(investor.getAge()).isEqualTo(0);
    }
}

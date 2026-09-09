package com.enviro.assessment.junior.fezile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * An investor who owns a portfolio of investment products.
 *
 * dateOfBirth is stored (rather than a raw age) so the investor's age is
 * always calculated accurately at the point a withdrawal rule needs it,
 * e.g. the "retirement withdrawals only allowed if age > 65" rule.
 */
@Entity
@Table(name = "investors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String email;

    @JsonIgnore
    @OneToOne(mappedBy = "investor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Portfolio portfolio;

    @JsonIgnore
    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WithdrawalNotice> withdrawalNotices = new ArrayList<>();

    /**
     * Calculates the investor's current age from their date of birth.
     * Used by withdrawal business rules (e.g. retirement age check).
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}

package com.enviro.assessment.junior.fezile.model;

import com.enviro.assessment.junior.fezile.model.enums.WithdrawalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A single investment product held within a portfolio (e.g. a retirement
 * annuity or a voluntary unit trust). Withdrawals are made against a
 * specific product's balance.
 *
 * `type` determines which withdrawal rules apply to this product's balance
 * (e.g. retirement products require investor age > 65).
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private WithdrawalType type;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;
}

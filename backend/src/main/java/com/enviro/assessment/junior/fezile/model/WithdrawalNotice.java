package com.enviro.assessment.junior.fezile.model;

import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A withdrawal notice submitted by an investor against one of their
 * products. Created only after passing the business rule checks
 * (age, balance sufficiency, 90% cap) applied in the service layer.
 */
@Entity
@Table(name = "withdrawal_notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    @JsonIgnore
    private Investor investor;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal amount;

    /**
     * Product balance snapshot at the time the notice was created, so
     * historical statements remain accurate even if the balance later
     * changes.
     */
    private BigDecimal balanceAfterWithdrawal;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;

    private LocalDateTime createdAt;
}

package com.engagetech.expenses.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "exchange_rate")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"date", "sourceCurrency", "targetCurrency"})
public final class ExchangeRate implements WithId {

    @Id
    @GeneratedValue(
            strategy = IDENTITY
    )
    private Long id;

    @Column(name = "rate_date", nullable = false, updatable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "source_currency_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Currency sourceCurrency;

    @ManyToOne
    @JoinColumn(name = "target_currency_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Currency targetCurrency;

    @Column(nullable = false, updatable = false)
    private BigDecimal rate;

    public ExchangeRate(LocalDate date, Currency sourceCurrency, Currency targetCurrency, BigDecimal rate) {
        this.date = date;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}

package com.engagetech.expenses.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAmount {

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    private Currency currency;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

}

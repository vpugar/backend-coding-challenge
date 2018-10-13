package com.engagetech.expenses.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VatData {

    @Column(name = "vat_rate", nullable = false, updatable = false)
    private BigDecimal vatRate;

    @Column(name = "vat_amount", nullable = false, updatable = false)
    private BigDecimal vatAmount;
}

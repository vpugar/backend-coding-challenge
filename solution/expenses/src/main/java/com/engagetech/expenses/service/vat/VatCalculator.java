package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.VatData;

import java.math.BigDecimal;

/**
 * Represents algorithm for vat calculation.
 */
public interface VatCalculator {

    VatData calculate(BigDecimal value);

}

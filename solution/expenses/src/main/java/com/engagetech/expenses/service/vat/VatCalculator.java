package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.VatData;

import java.math.BigDecimal;

public interface VatCalculator {

    VatData calculate(BigDecimal value);

}

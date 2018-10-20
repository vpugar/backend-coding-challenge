package com.engagetech.expenses.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Value
public class VatCalculationDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal vatAmount;

    @NotNull
    private BigDecimal vatRate;

    private CurrencyDTO currency;

    private boolean exchangeCalculation;
}

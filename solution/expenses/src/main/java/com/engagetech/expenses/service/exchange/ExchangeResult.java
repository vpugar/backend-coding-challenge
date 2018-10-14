package com.engagetech.expenses.service.exchange;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Value
public class ExchangeResult {

    private final LocalDate date;
    private final BigDecimal rate;
    private final CurrencyAmount sourceAmount;
    private final CurrencyAmount targetAmount;

    public ExchangeResult(LocalDate date, BigDecimal rate, CurrencyAmount sourceAmount,
          Currency targetCurrency) {
        this.date = date;
        this.rate = rate;
        this.sourceAmount = sourceAmount;
        targetAmount = new CurrencyAmount(targetCurrency, calculateTargetAmount(targetCurrency));
    }

    private BigDecimal calculateTargetAmount(Currency targetCurrency) {
        return rate
              .multiply(sourceAmount.getAmount())
              .setScale(targetCurrency.getScale(), RoundingMode.HALF_UP);
    }
}

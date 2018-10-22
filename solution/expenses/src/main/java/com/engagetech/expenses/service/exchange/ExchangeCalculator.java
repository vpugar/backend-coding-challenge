package com.engagetech.expenses.service.exchange;

import com.engagetech.expenses.model.CurrencyAmount;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Calculate target currency based on input currency and date.
 */
public interface ExchangeCalculator {

    Optional<ExchangeResult> calculate(LocalDate date, CurrencyAmount sourceCurrencyAmount);

}

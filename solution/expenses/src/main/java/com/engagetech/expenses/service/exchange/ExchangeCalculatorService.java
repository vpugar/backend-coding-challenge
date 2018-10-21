package com.engagetech.expenses.service.exchange;


import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.service.currency.UnknownCurrencyException;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeCalculatorService extends ExchangeCalculator {

    void saveResult(ExchangeResult exchangeResult);

    Optional<ExchangeRateDTO> getExchangeRate(LocalDate date, String sourceCurrencyShortName) throws UnknownCurrencyException;

}

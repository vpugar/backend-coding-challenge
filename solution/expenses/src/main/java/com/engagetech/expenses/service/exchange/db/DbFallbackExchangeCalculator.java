package com.engagetech.expenses.service.exchange.db;

import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeCalculatorService;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Fallback based implementation of exchange of currencies. Fallback is done from ExchangeCalculator and
 * storage based ExchangeCalculatorService.
 */
@Service("exchangeCalculator")
public class DbFallbackExchangeCalculator implements ExchangeCalculator {

    private final ExchangeCalculatorService exchangeCalculatorService;
    private final ExchangeCalculator fallbackExchangeCalculator;

    public DbFallbackExchangeCalculator(
            @Qualifier("dbExchangeCalculatorService") ExchangeCalculatorService exchangeCalculatorService,
            @Qualifier("FCCApiExchangeCalculator") ExchangeCalculator fallbackExchangeCalculator) {
        this.exchangeCalculatorService = exchangeCalculatorService;
        this.fallbackExchangeCalculator = fallbackExchangeCalculator;
    }

    @Override
    public Optional<ExchangeResult> calculate(LocalDate date, CurrencyAmount sourceCurrencyAmount) {
        Optional<ExchangeResult> dbResult = exchangeCalculatorService.calculate(date, sourceCurrencyAmount);

        if (dbResult.isPresent()) {
            return dbResult;
        } else {
            return fallbackExchangeCalculator.calculate(date, sourceCurrencyAmount)
                    .map(exchangeResult -> {
                        exchangeCalculatorService.saveResult(exchangeResult);
                        return exchangeResult;
                    });
        }

    }
}

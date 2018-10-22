package com.engagetech.expenses.service.exchange.fcc;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * The Free Currency Converter API based implementation of exchange of currencies.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class FCCApiExchangeCalculator implements ExchangeCalculator {

    private final FCCApiClient fccApiClient;
    private final DefaultCurrency defaultCurrency;

    @Override
    public Optional<ExchangeResult> calculate(LocalDate date, CurrencyAmount sourceCurrencyAmount) {

        Currency targetCurrency = defaultCurrency.getTarget();

        if (targetCurrency.equals(sourceCurrencyAmount.getCurrency())) {
            return Optional.of(new ExchangeResult(date, BigDecimal.ONE, sourceCurrencyAmount, targetCurrency));
        }

        try {
            log.debug("Getting exchange rate for pair {}_{}", sourceCurrencyAmount.getCurrency(), targetCurrency);

            final Optional<BigDecimal> rateOptional = fccApiClient.convert(
                    date, sourceCurrencyAmount.getCurrency(), targetCurrency);

            return rateOptional.map(rate -> new ExchangeResult(date, rate, sourceCurrencyAmount, targetCurrency));
        } catch (RuntimeException e) {
            log.warn("FCC API not working", e);
            return Optional.empty();
        }
    }
}

package com.engagetech.expenses.service.exchange.db;

import com.engagetech.expenses.TestCurrencies;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeCalculatorService;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.engagetech.expenses.TestCurrencies.EUR_GBP_RATE;
import static com.engagetech.expenses.TestCurrencies.gbpCurrency;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DbFallbackExchangeCalculatorTest {

    private DbFallbackExchangeCalculator dbFallbackExchangeCalculator;

    @Mock
    private ExchangeCalculatorService exchangeCalculatorService;
    @Mock
    private ExchangeCalculator fallbackExchangeCalculator;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        dbFallbackExchangeCalculator = new DbFallbackExchangeCalculator(exchangeCalculatorService,
                fallbackExchangeCalculator);
    }

    @Test
    public void givenEurCurrencyAndResultFromDbServiceWhenCalculateThenExchangeResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        when(exchangeCalculatorService.calculate(today, currencyAmount))
                .thenReturn(Optional.of(new ExchangeResult(today, EUR_GBP_RATE, currencyAmount, gbpCurrency)));

        // action
        Optional<ExchangeResult> resultOptional = dbFallbackExchangeCalculator.calculate(today, currencyAmount);

        // assert
        assertTrue(resultOptional.isPresent());
    }

    @Test
    public void givenEurCurrencyAndResultFromFallbackCalculatorWhenCalculateThenExchangeResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        when(exchangeCalculatorService.calculate(today, currencyAmount))
                .thenReturn(Optional.empty());
        when(fallbackExchangeCalculator.calculate(today, currencyAmount))
                .thenReturn(Optional.of(new ExchangeResult(today, EUR_GBP_RATE, currencyAmount, gbpCurrency)));

        // action
        Optional<ExchangeResult> resultOptional = dbFallbackExchangeCalculator.calculate(today, currencyAmount);

        // assert
        assertTrue(resultOptional.isPresent());
        verify(exchangeCalculatorService).saveResult(resultOptional.get());
    }

    @Test
    public void givenNoResultFromServicesWhenCalculateThenNoResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        when(exchangeCalculatorService.calculate(today, currencyAmount))
                .thenReturn(Optional.empty());
        when(fallbackExchangeCalculator.calculate(today, currencyAmount))
                .thenReturn(Optional.empty());

        // action
        Optional<ExchangeResult> resultOptional = dbFallbackExchangeCalculator.calculate(today, currencyAmount);

        // assert
        assertFalse(resultOptional.isPresent());
    }
}

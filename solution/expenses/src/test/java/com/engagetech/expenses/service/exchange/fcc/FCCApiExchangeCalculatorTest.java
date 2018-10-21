package com.engagetech.expenses.service.exchange.fcc;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static com.engagetech.expenses.TestCurrencies.EUR_GBP_RATE;
import static com.engagetech.expenses.TestCurrencies.eurCurrency;
import static com.engagetech.expenses.TestCurrencies.gbpCurrency;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.noMoreInteractions;

public class FCCApiExchangeCalculatorTest {

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Mock
    private FCCApiClient fccApiClient;
    @Mock
    private DefaultCurrency defaultCurrency;

    private FCCApiExchangeCalculator fccApiExchangeCalculator;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(gbpCurrency);
        fccApiExchangeCalculator = new FCCApiExchangeCalculator(fccApiClient, defaultCurrency);
    }

    @Test
    public void givenEurCurrencyWhenCalculateThenExchangeResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(eurCurrency, new BigDecimal("100.00"));
        final Currency targetCurrency = defaultCurrency.getTarget();
        when(fccApiClient.convert(today, eurCurrency, targetCurrency))
                .thenReturn(Optional.of(EUR_GBP_RATE));

        // action
        Optional<ExchangeResult> resultOptional = fccApiExchangeCalculator.calculate(today, currencyAmount);

        // assert
        errorCollector.checkThat(resultOptional.isPresent(), is(true));
        ExchangeResult exchangeResult = resultOptional.get();
        errorCollector.checkThat(exchangeResult.getDate(), is(today));
        errorCollector.checkThat(exchangeResult.getRate(), is(EUR_GBP_RATE));
        errorCollector.checkThat(exchangeResult.getSourceAmount(), is(currencyAmount));
        errorCollector.checkThat(exchangeResult.getTargetAmount().getCurrency(), is(targetCurrency));
        errorCollector.checkThat(exchangeResult.getTargetAmount().getAmount(),
                is(EUR_GBP_RATE.multiply(currencyAmount.getAmount())
                        .setScale(targetCurrency.getScale(), RoundingMode.HALF_UP)));
    }

    @Test
    public void givenNotWorkingExchangeClientWhenCalculateThenNoResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(eurCurrency, new BigDecimal("100.00"));
        final Currency targetCurrency = defaultCurrency.getTarget();
        when(fccApiClient.convert(today, eurCurrency, targetCurrency))
                .thenThrow(RuntimeException.class);

        // action
        final Optional<ExchangeResult> resultOptional = fccApiExchangeCalculator.calculate(today, currencyAmount);

        // assert
        assertFalse(resultOptional.isPresent());
    }

    @Test
    public void givenSameCurrencyWhenCalculateThenReturnRateEqual1() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(defaultCurrency.getTarget(), new BigDecimal("100.00"));
        verify(fccApiClient, noMoreInteractions())
                .convert(anyObject(), anyObject(), anyObject());

        // action
        final Optional<ExchangeResult> resultOptional = fccApiExchangeCalculator.calculate(today, currencyAmount);

        // assert
        errorCollector.checkThat(resultOptional.isPresent(), is(true));
        ExchangeResult exchangeResult = resultOptional.get();
        errorCollector.checkThat(exchangeResult.getDate(), is(today));
        errorCollector.checkThat(exchangeResult.getRate(), is(BigDecimal.ONE));
        errorCollector.checkThat(exchangeResult.getSourceAmount(), is(currencyAmount));
        errorCollector.checkThat(exchangeResult.getTargetAmount(), is(currencyAmount));
    }
}

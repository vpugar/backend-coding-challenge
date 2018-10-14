package com.engagetech.expenses.service.exchange.db;

import com.engagetech.expenses.TestCurrencies;
import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.mapper.ExchangeRateMapper;
import com.engagetech.expenses.mapper.ExchangeRateMapperImpl;
import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.model.ExchangeRate;
import com.engagetech.expenses.repository.ExchangeRateRepository;
import com.engagetech.expenses.service.currency.CurrencyService;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static com.engagetech.expenses.TestCurrencies.EUR_GBP_RATE;
import static com.engagetech.expenses.TestCurrencies.eurCurrency;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.noMoreInteractions;

public class DbExchangeCalculatorServiceTest {

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapperImpl();

    @Mock
    private ExchangeRateRepository exchangeRateRepository;
    @Mock
    private DefaultCurrency defaultCurrency;
    @Mock
    private CurrencyService currencyService;

    private DbExchangeCalculatorService dbExchangeCalculatorService;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(TestCurrencies.gbpCurrency);
        dbExchangeCalculatorService = new DbExchangeCalculatorService(
                exchangeRateRepository, exchangeRateMapper, defaultCurrency, currencyService);
    }

    @Test
    public void givenEurCurrencyWhenCalculateThenExchangeResult() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        final Currency targetCurrency = defaultCurrency.getTarget();
        when(exchangeRateRepository.findByDateAndSourceCurrencyAndTargetCurrency(
                today, currencyAmount.getCurrency(), targetCurrency))
                .thenReturn(Optional.of(new ExchangeRate(today, eurCurrency, targetCurrency, EUR_GBP_RATE)));

        // action
        Optional<ExchangeResult> resultOptional = dbExchangeCalculatorService.calculate(today, currencyAmount);

        // assert
        assertTrue(resultOptional.isPresent());
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
    public void givenNoResultFromDbWhenCalculateThenNoResultFromCalculate() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        Currency targetCurrency = defaultCurrency.getTarget();
        when(exchangeRateRepository.findByDateAndSourceCurrencyAndTargetCurrency(
                today, currencyAmount.getCurrency(), targetCurrency))
                .thenReturn(Optional.empty());

        // action
        Optional<ExchangeResult> resultOptional = dbExchangeCalculatorService.calculate(today, currencyAmount);

        // assert
        assertFalse(resultOptional.isPresent());
    }

    @Test
    public void givenSameCurrencyWhenCalculateThenReturnRateEqual1() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(defaultCurrency.getTarget(), new BigDecimal("100.00"));
        verify(exchangeRateRepository, noMoreInteractions())
                .findByDateAndSourceCurrencyAndTargetCurrency(anyObject(), anyObject(), anyObject());

        // action
        final Optional<ExchangeResult> resultOptional = dbExchangeCalculatorService.calculate(today, currencyAmount);

        // assert
        errorCollector.checkThat(resultOptional.isPresent(), is(true));
        ExchangeResult exchangeResult = resultOptional.get();
        errorCollector.checkThat(exchangeResult.getDate(), is(today));
        errorCollector.checkThat(exchangeResult.getRate(), is(BigDecimal.ONE));
        errorCollector.checkThat(exchangeResult.getSourceAmount(), is(currencyAmount));
        errorCollector.checkThat(exchangeResult.getTargetAmount(), is(currencyAmount));
    }

    @Test
    public void givenExchangeResultWhenSaveResultThenDoSave() {
        // arrange
        LocalDate today = LocalDate.now();
        CurrencyAmount currencyAmount = new CurrencyAmount(TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        Currency targetCurrency = defaultCurrency.getTarget();
        ExchangeResult exchangeResult = new ExchangeResult(
                today,
                EUR_GBP_RATE,
                currencyAmount,
                targetCurrency
        );
        ArgumentCaptor<ExchangeRate> expenseCaptor = ArgumentCaptor.forClass(ExchangeRate.class);

        // action
        dbExchangeCalculatorService.saveResult(exchangeResult);

        // assert
        verify(exchangeRateRepository).save(expenseCaptor.capture());
        ExchangeRate exchangeRate = expenseCaptor.getValue();
        errorCollector.checkThat(exchangeRate.getRate(), is(EUR_GBP_RATE));
        errorCollector.checkThat(exchangeRate.getDate(), is(today));
        errorCollector.checkThat(exchangeRate.getSourceCurrency(), is(currencyAmount.getCurrency()));
        errorCollector.checkThat(exchangeRate.getTargetCurrency(), is(targetCurrency));
    }

    @Test
    public void givenExchangeRateInDbWhenGetExchangeRateThenExchangeRate() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        Currency sourceCurrency = TestCurrencies.eurCurrency.clone();
        sourceCurrency.setId(1L);
        Currency targetCurrency = defaultCurrency.getTarget().clone();
        targetCurrency.setId(1L);
        when(currencyService.getCurrency(sourceCurrency.getShortName()))
                .thenReturn(sourceCurrency);
        ExchangeRate exchangeRate = new ExchangeRate(today, sourceCurrency, targetCurrency, EUR_GBP_RATE);
        exchangeRate.setId(1L);
        when(exchangeRateRepository
                .findByDateAndSourceCurrencyAndTargetCurrency(today, sourceCurrency, targetCurrency))
                .thenReturn(Optional.of(exchangeRate));

        // action
        Optional<ExchangeRateDTO> exchangeRateOptional =
                dbExchangeCalculatorService.getExchangeRate(today, sourceCurrency.getShortName());

        // assert
        errorCollector.checkThat(exchangeRateOptional.isPresent(), is(true));
        ExchangeRateDTO exchangeRateDTO = exchangeRateOptional.get();
        errorCollector.checkThat(exchangeRateDTO.getId(), is(1L));
        errorCollector.checkThat(exchangeRateDTO.getDate(), is(today));
        errorCollector.checkThat(exchangeRateDTO.getRate(), is(EUR_GBP_RATE));
        errorCollector.checkThat(exchangeRateDTO.getSourceCurrencyId(), is(sourceCurrency.getId()));
        errorCollector.checkThat(exchangeRateDTO.getTargetCurrencyId(), is(targetCurrency.getId()));
    }

    @Test
    public void givenNoExchangeRateInDbWhenGetExchangeRateThenNoExchangeRate() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        Currency sourceCurrency = TestCurrencies.eurCurrency;
        Currency targetCurrency = defaultCurrency.getTarget();
        when(currencyService.getCurrency(sourceCurrency.getShortName()))
                .thenReturn(sourceCurrency);
        ExchangeRate exchangeRate = new ExchangeRate(today, sourceCurrency, targetCurrency, EUR_GBP_RATE);
        exchangeRate.setId(1L);
        when(exchangeRateRepository
                .findByDateAndSourceCurrencyAndTargetCurrency(today, sourceCurrency, targetCurrency))
                .thenReturn(Optional.empty());

        // action
        Optional<ExchangeRateDTO> exchangeRateOptional =
                dbExchangeCalculatorService.getExchangeRate(today, sourceCurrency.getShortName());

        // assert
        errorCollector.checkThat(exchangeRateOptional.isPresent(), is(false));
    }
}

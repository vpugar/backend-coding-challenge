package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.TestCurrencies;
import com.engagetech.expenses.model.CurrencyAmount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CurrencyAmountParserParameterizedTest {

    @Parameter
    public String value;
    @Parameter(1)
    public BigDecimal expected;
    @Parameter(2)
    public String currencyShortName;
    @Mock
    private DefaultCurrency defaultCurrency;
    @Mock
    private CurrencyService currencyService;
    private CurrencyAmountParser currencyAmountParser;

    @Parameters(name = "{index}: value={0}")
    public static Object[][] data() {
        return new Object[][]{
                {"100", new BigDecimal("100.00"), "GBP"},
                {"100.0", new BigDecimal("100.00"), "GBP"},
                {"120.00", new BigDecimal("120.00"), "GBP"},
                {"120 EUR", new BigDecimal("120.00"), "EUR"},
                {"120.00 EUR", new BigDecimal("120.00"), "EUR"},
                {"EUR 120.00", new BigDecimal("120.00"), "EUR"},
                {" EUR 120.00 ", new BigDecimal("120.00"), "EUR"},
                {"   EUR   120.00   ", new BigDecimal("120.00"), "EUR"}
        };
    }

    @Before
    public void prepare() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(TestCurrencies.gbpCurrency);
        when(defaultCurrency.getSupported()).thenReturn(Collections.singleton(TestCurrencies.eurCurrency));
        when(currencyService.getCurrency("EUR")).thenReturn(TestCurrencies.eurCurrency);
        when(currencyService.getCurrency("EUR")).thenReturn(TestCurrencies.eurCurrency);
        currencyAmountParser = new CurrencyAmountParser(defaultCurrency, currencyService);
    }

    @Test
    public void givenValueWhenThen() throws Exception {
        // action
        CurrencyAmount currencyAmount = currencyAmountParser.parse(value);

        // assert
        assertThat(currencyAmount.getCurrency(), notNullValue());
        assertThat(currencyAmount.getCurrency().getShortName(), is(currencyShortName));
        assertThat(currencyAmount.getAmount(), notNullValue());
        assertThat(currencyAmount.getAmount(), is(expected));
        assertThat(currencyAmount.getCurrency().getScale(), is(expected.scale()));
    }
}

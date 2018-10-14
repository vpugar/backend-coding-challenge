package com.engagetech.expenses.service.currency;

import static org.mockito.Mockito.when;

import com.engagetech.expenses.TestCurrencies;
import com.engagetech.expenses.model.Currency;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

public class CurrencyAmountParserTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DefaultCurrency defaultCurrency;
    @Mock
    private CurrencyService currencyService;

    private CurrencyAmountParser currencyAmountParser;

    @Before
    public void prepare() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(new Currency("GBP", 2));
        when(defaultCurrency.getSupported()).thenReturn(Collections.singleton(TestCurrencies.eurCurrency));
        when(currencyService.getCurrency("EUR")).thenReturn(TestCurrencies.eurCurrency);
        when(currencyService.getCurrency("USD")).thenReturn(TestCurrencies.usdCurrency);
        when(currencyService.getCurrency("UNW")).thenThrow(new UnknownCurrencyException("UNW"));
        currencyAmountParser = new CurrencyAmountParser(defaultCurrency, currencyService);
    }

    @Test
    public void givenEmptyStringWhenParseThenNoAmountException() throws Exception {
        // assert
        expectedException.expect(NoAmountException.class);
        expectedException.expectMessage("amountWithCurrency is blank");

        currencyAmountParser.parse("");
    }

    @Test
    public void givenStringWithSpacesWhenParseThenNoAmountException() throws Exception {
        // assert
        expectedException.expect(NoAmountException.class);
        expectedException.expectMessage("amountWithCurrency is blank");

        currencyAmountParser.parse("    ");
    }

    @Test
    public void givenNotValidStringWhenParseThenNoAmountException() throws Exception {
        // assert
        expectedException.expect(NoAmountException.class);
        expectedException.expectMessage("No amount in expression");

        currencyAmountParser.parse("this is not valid");
    }

    @Test
    public void givenNotValidString2WhenParseThenNoAmountException() throws Exception {
        // assert
        expectedException.expect(NoAmountException.class);
        expectedException.expectMessage("Cannot parse amount");

        currencyAmountParser.parse("thisisnotvalid");
    }


}

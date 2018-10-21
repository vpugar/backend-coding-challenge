package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DefaultCurrencyTest {

    private static final String DEFAULT_CURRENCY_SHORT_NAME = "GBP";
    private static final String CURRENCY_SHORT_NAME_1 = "USD";
    private static final String CURRENCY_SHORT_NAME_2 = "EUR";
    private static final String CURRENCY_SHORT_NAME_3 = "CHF";
    private static final List<String> ALL_SHORT_NAMES = Collections.unmodifiableList(Arrays.asList(
            CURRENCY_SHORT_NAME_1, CURRENCY_SHORT_NAME_2, CURRENCY_SHORT_NAME_3,
            DEFAULT_CURRENCY_SHORT_NAME));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    @Mock
    private CurrencyService currencyService;

    private DefaultCurrency defaultCurrency;

    @Before
    public void prepare() throws Exception {
        MockitoAnnotations.initMocks(this);

        ALL_SHORT_NAMES.forEach(s -> {
            try {
                when(currencyService.getCurrency(s)).thenReturn(new Currency(s, 2));
            } catch (UnknownCurrencyException e) {
                throw new RuntimeException(e);
            }
        });

        defaultCurrency = new DefaultCurrency(currencyService, DEFAULT_CURRENCY_SHORT_NAME,
                ALL_SHORT_NAMES.toArray(new String[0]));
        defaultCurrency.init();
    }

    @Test
    public void givenSetupWithDefaultCurrencyWhenGetTargetThenDefaultCurrency() {
        // action
        Currency target = defaultCurrency.getTarget();

        // assert
        assertThat(target.getShortName(), is(DEFAULT_CURRENCY_SHORT_NAME));
    }

    @Test
    public void givenSetupWithSupportedWhenGetSupportedThenSetWithSupported() {
        // arrange
        List<String> expectedSupported = ALL_SHORT_NAMES;

        // action
        Set<Currency> supported = defaultCurrency.getSupported();

        // assert
        List<String> supportedShortNames = supported.stream().map(Currency::getShortName).collect(Collectors.toList());
        assertThat(supportedShortNames, containsInAnyOrder(expectedSupported.toArray()));
    }

    @Test
    public void givenNoCurrencyInDbWhenInitThenUnknownCurrencyException() throws Exception {
        // arrange
        when(currencyService.getCurrency(CURRENCY_SHORT_NAME_3)).thenAnswer(invocation -> {
            throw new UnknownCurrencyException("No currency " + invocation.getArgument(0));
        });
        defaultCurrency = new DefaultCurrency(currencyService, DEFAULT_CURRENCY_SHORT_NAME,
                ALL_SHORT_NAMES.toArray(new String[0]));

        // assert
        expectedException.expect(UnknownCurrencyException.class);
        expectedException.expectMessage("No currency " + CURRENCY_SHORT_NAME_3);

        // action
        defaultCurrency.init();
    }
}

package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.repository.CurrencyRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class DbCurrencyServiceTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    @Mock
    private CurrencyRepository currencyRepository;

    private CurrencyService currencyService;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        currencyService = new DbCurrencyService(currencyRepository);
    }

    @Test
    public void givenUnknownShortNameWhenGetCurrencyThenUnknownCurrencyException() throws Exception {
        // assert
        expectedException.expect(UnknownCurrencyException.class);
        expectedException.expectMessage("Currency unknownShortName not found");

        // action
        currencyService.getCurrency("unknownShortName");
    }

    @Test
    public void givenKnownShortNameWhenGetCurrencyThenCurrency() throws Exception {
        // arrange
        final String shortName = "knownShortName";
        when(currencyRepository.findByShortName(shortName)).thenReturn(Optional.of(new Currency(shortName, 2)));

        // action
        Currency currency = currencyService.getCurrency(shortName);

        // assert
        assertThat(currency, notNullValue());
        assertThat(currency.getShortName(), is(shortName));
    }

    @Test
    public void givenNullWhenGetCurrencyThenNullPointerException() throws Exception {
        // assert
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("shortName is marked @NonNull but is null");

        // action
        currencyService.getCurrency(null);
    }
}

package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

public class SalesTaxVatCalculatorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    @Mock
    private DefaultCurrency defaultCurrency;

    private VatCalculator vatCalculator;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(new Currency("TST", 3));
        vatCalculator = new SalesTaxVatCalculator(defaultCurrency, new BigDecimal("20"));
    }

    @Test
    public void givenNullWhenCalculateThenNullPointerException() {
        // assert
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("value is marked @NonNull but is null");

        vatCalculator.calculate(null);
    }
}

package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.VatData;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SalesTaxVatCalculatorParameterizedTest {

    @Mock
    private DefaultCurrency defaultCurrency;

    private VatCalculator vatCalculator;

    @Parameter
    public BigDecimal vat;
    @Parameter(1)
    public int scale;
    @Parameter(2)
    public BigDecimal value;
    @Parameter(3)
    public BigDecimal expected;

    @Parameters(name = "{index}: tax={0} scale={1} value={3}")
    public static Object[][] data() {
        return new Object[][]{
                {new BigDecimal("20"), 3, new BigDecimal("100"), new BigDecimal("20.000")},
                {new BigDecimal("20"), 3, new BigDecimal("123.456"), new BigDecimal("24.691")},
                {new BigDecimal("20"), 3, new BigDecimal("123.999"), new BigDecimal("24.800")},
                {new BigDecimal("20"), 3, new BigDecimal("55.555"), new BigDecimal("11.111")}
        };
    }

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(new Currency("TST", scale));
        vatCalculator = new SalesTaxVatCalculator(defaultCurrency, vat);
    }

    @Test
    public void givenValueWhenCalculateThenExpected() {
        // action
        VatData result = vatCalculator.calculate(value);

        // assert
        Assert.assertThat(result.getVatAmount(), is(expected));
        Assert.assertThat(result.getVatRate(), is(vat));
    }
}

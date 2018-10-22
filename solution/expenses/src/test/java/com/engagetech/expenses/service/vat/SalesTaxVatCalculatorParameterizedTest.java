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

    @Parameter
    public BigDecimal vat;
    @Parameter(1)
    public int scale;
    @Parameter(2)
    public BigDecimal value;
    @Parameter(3)
    public BigDecimal expected;
    @Mock
    private DefaultCurrency defaultCurrency;
    private SalesTaxVatCalculator vatCalculator;

    @Parameters(name = "{index}: tax={0} scale={1} value={3}")
    public static Object[][] data() {
        return new Object[][]{
                {new BigDecimal("20"), 3, new BigDecimal("100"), new BigDecimal("16.667")},
                {new BigDecimal("20"), 3, new BigDecimal("123.456"), new BigDecimal("20.576")},
                {new BigDecimal("20"), 3, new BigDecimal("123.999"), new BigDecimal("20.667")},
                {new BigDecimal("20"), 3, new BigDecimal("55.555"), new BigDecimal("9.259")},
                {new BigDecimal("20"), 2, new BigDecimal("100"), new BigDecimal("16.67")},
                {new BigDecimal("20"), 2, new BigDecimal("100.00"), new BigDecimal("16.67")},
                {new BigDecimal("20"), 2, new BigDecimal("44.44"), new BigDecimal("7.41")},
                {new BigDecimal("20"), 2, new BigDecimal("33.33"), new BigDecimal("5.56")},
                {new BigDecimal("20"), 2, new BigDecimal("99.99"), new BigDecimal("16.67")},
                {new BigDecimal("20"), 2, new BigDecimal("100000000000000"), new BigDecimal("16666666666666.67")}
        };
    }

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        when(defaultCurrency.getTarget()).thenReturn(new Currency("TST", scale));
        vatCalculator = new SalesTaxVatCalculator(defaultCurrency, vat);
        vatCalculator.init();
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

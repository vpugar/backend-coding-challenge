package com.engagetech.expenses;

import com.engagetech.expenses.model.Currency;

import java.math.BigDecimal;

public final class TestCurrencies {

    public static final Currency gbpCurrency = new Currency("GBP", 2);
    public static final Currency eurCurrency = new Currency("EUR", 2);
    public static final Currency usdCurrency = new Currency("USD", 2);
    public static final BigDecimal EUR_GBP_RATE = new BigDecimal("0.881533");

    private TestCurrencies() {
        throw new UnsupportedOperationException("Cannot use constructor");
    }

}

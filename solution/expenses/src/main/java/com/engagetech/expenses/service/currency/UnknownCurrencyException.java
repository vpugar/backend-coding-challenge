package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.service.ExpenseProcessException;

public final class UnknownCurrencyException extends ExpenseProcessException {

    private static final long serialVersionUID = -8480480505168211322L;

    public UnknownCurrencyException(String message) {
        super(message);
    }
}

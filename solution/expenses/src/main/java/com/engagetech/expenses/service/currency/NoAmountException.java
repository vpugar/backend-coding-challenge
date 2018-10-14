package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.service.ExpenseProcessException;

public final class NoAmountException extends ExpenseProcessException {

    private static final long serialVersionUID = 1793214053773553089L;

    public NoAmountException(String message) {
        super(message);
    }
}

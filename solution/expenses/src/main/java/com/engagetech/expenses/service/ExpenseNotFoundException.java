package com.engagetech.expenses.service;

public final class ExpenseNotFoundException extends ExpenseProcessException {

    private static final long serialVersionUID = 8651325719346178703L;

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}

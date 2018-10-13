package com.engagetech.expenses.service;

public final class UserNotFoundException extends ExpenseProcessException {

    private static final long serialVersionUID = 8066607836684114376L;

    public UserNotFoundException(String message) {
        super(message);
    }
}

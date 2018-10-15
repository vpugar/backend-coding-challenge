package com.engagetech.expenses.service;

public final class DateOutOfBoundsException extends ExpenseProcessException {

    private static final long serialVersionUID = 9130782323565457814L;

    public DateOutOfBoundsException(String message) {
        super(message);
    }
}

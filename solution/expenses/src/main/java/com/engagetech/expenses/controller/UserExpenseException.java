package com.engagetech.expenses.controller;

public class UserExpenseException extends Exception {

    private static final long serialVersionUID = 2611075259250816365L;

    public UserExpenseException(String message) {
        super(message);
    }
}

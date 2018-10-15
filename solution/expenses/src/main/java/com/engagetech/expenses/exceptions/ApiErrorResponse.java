package com.engagetech.expenses.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiErrorResponse {

    private final Exception exception;

    public String getType() {
        return exception.getClass().getSimpleName();
    }

    public String getMessage() {
        return exception.getMessage();
    }
}

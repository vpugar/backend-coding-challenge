package com.engagetech.expenses.exceptions;

import com.engagetech.expenses.service.ExpenseNotFoundException;
import com.engagetech.expenses.service.ExpenseProcessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleExpenseNotFoundException(ExpenseNotFoundException e) {
        return new ApiErrorResponse(e);
    }

    @ExceptionHandler({ExpenseProcessException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleExpenseProcessException(Exception e) {
        return new ApiErrorResponse(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse unknownException(Exception e) {
        return new ApiErrorResponse(e);
    }
}

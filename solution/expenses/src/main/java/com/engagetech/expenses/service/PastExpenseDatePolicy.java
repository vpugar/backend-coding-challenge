package com.engagetech.expenses.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class PastExpenseDatePolicy implements ExpenseDatePolicy {

    private final int allowedDaysInPast;

    public PastExpenseDatePolicy(
            @Value("${app.expense.expense-date-policy.allowed-days-in-past.days}") int allowedDaysInPast) {
        this.allowedDaysInPast = allowedDaysInPast;
    }

    @Override
    public void check(@NonNull LocalDate date) throws DateOutOfBoundsException {
        LocalDate now = LocalDate.now();
        if (now.isBefore(date)) {
            throw new DateOutOfBoundsException("Date is after current date");
        }
        LocalDate allowedMinimalDate = now.minus(allowedDaysInPast, ChronoUnit.DAYS);
        if (allowedMinimalDate.isAfter(date)) {
            throw new DateOutOfBoundsException("Date is before allowed date");
        }
    }
}
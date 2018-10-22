package com.engagetech.expenses.service;

import java.time.LocalDate;

/**
 * Validates expense input date for specific bounds.
 */
public interface ExpenseDatePolicy {

    void check(LocalDate date) throws DateOutOfBoundsException;

}
package com.engagetech.expenses.service;

import java.time.LocalDate;

public interface ExpenseDatePolicy {

    void check(LocalDate date) throws DateOutOfBoundsException;

}
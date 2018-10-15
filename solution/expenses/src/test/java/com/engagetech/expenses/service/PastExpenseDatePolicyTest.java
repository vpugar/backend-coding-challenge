package com.engagetech.expenses.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;

public class PastExpenseDatePolicyTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private final ExpenseDatePolicy expenseDatePolicy = new PastExpenseDatePolicy(10);

    @Test
    public void givenCurrentDateWhenCheckThenOk() throws Exception {
        // action
        expenseDatePolicy.check(LocalDate.now());

        // assert
        // expected OK - none exception
    }

    @Test
    public void givenPastDateBetweenBoundsWhenCheckThenOk() throws Exception {
        // action
        expenseDatePolicy.check(LocalDate.now().minusDays(9));

        // assert
        // expected exception none
    }

    @Test
    public void givenPastDateOnBoundWhenCheckThenOk() throws Exception {
        // action
        expenseDatePolicy.check(LocalDate.now().minusDays(10));

        // assert
        // expected exception none
    }

    @Test
    public void givenFutureDateWhenCheckThenOkDateOutOfBoundsException() throws Exception {
        // assert
        expectedException.expect(DateOutOfBoundsException.class);
        expectedException.expectMessage("Date is after current date");

        // action
        expenseDatePolicy.check(LocalDate.now().plusDays(1));
    }

    @Test
    public void givenPastDate11DaysWhenCheckThenOkDateOutOfBoundsException() throws Exception {
        // assert
        expectedException.expect(DateOutOfBoundsException.class);
        expectedException.expectMessage("Date is before allowed date");

        // action
        expenseDatePolicy.check(LocalDate.now().minusDays(11));
    }

    @Test
    public void givenNullWhenCheckThenNullPointerException() throws Exception {
        // assert
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("date is marked @NonNull but is null");

        // action
        expenseDatePolicy.check(null);
    }
}

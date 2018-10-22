package com.engagetech.expenses.service;

import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.dto.VatCalculationDTO;

import java.util.List;

/**
 * Expense service for processing input expenses, calculation of vat and exchange on the expense.
 * It lists expenses by user and gets expense based on expense ID.
 */
public interface UserExpenseService {

    ExpenseDTO process(long userId, AddExpenseCommand command) throws ExpenseProcessException;

    VatCalculationDTO calculate(CalculateVatCommand command) throws ExpenseProcessException;

    List<ExpenseDTO> getUserExpenses(long userId) throws UserNotFoundException;

    ExpenseDTO getUserExpense(long expenseId) throws ExpenseNotFoundException;

}

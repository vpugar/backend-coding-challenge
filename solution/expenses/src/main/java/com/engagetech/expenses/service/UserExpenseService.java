package com.engagetech.expenses.service;

import com.engagetech.expenses.dto.ExpenseDTO;

import java.util.List;

public interface UserExpenseService {

    ExpenseDTO process(long userId, AddExpenseCommand command) throws ExpenseProcessException;

    List<ExpenseDTO> getUserExpenses(long userId) throws UserNotFoundException;
}

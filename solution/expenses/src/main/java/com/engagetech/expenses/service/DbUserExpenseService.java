package com.engagetech.expenses.service;


import com.engagetech.expenses.dto.ExpenseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DbUserExpenseService implements UserExpenseService {

    @Override
    public ExpenseDTO process(long userId, AddExpenseCommand command) throws ExpenseProcessException {
        return null;
    }

    @Override
    public List<ExpenseDTO> getUserExpenses(long userId) throws UserNotFoundException {
        return null;
    }
}

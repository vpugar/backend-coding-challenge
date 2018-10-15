package com.engagetech.expenses.service;


import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.mapper.CurrencyMapper;
import com.engagetech.expenses.mapper.ExpenseMapper;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.User;
import com.engagetech.expenses.repository.ExpenseRepository;
import com.engagetech.expenses.service.currency.CurrencyAmountParser;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import com.engagetech.expenses.service.vat.VatCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DbUserExpenseService implements UserExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserService userService;
    private final CurrencyAmountParser currencyAmountParser;
    private final ExpenseDatePolicy expenseDatePolicy;
    private final ExchangeCalculator exchangeCalculator;
    private final VatCalculator vatCalculator;

    @Override
    @Transactional
    public ExpenseDTO process(long userId, @NonNull AddExpenseCommand command) throws ExpenseProcessException {

        expenseDatePolicy.check(command.getDate());

        User user = userService.getUser(userId);
        CurrencyAmount currencyAmount = currencyAmountParser.parse(command.getAmount());
        ExchangeResult exchangeResult = exchangeCalculator
                .calculate(command.getDate(), currencyAmount)
                .orElseThrow(() -> new ExchangeProcessException("Cannot calculate exchange for " +
                        currencyAmount.getCurrency()));

        Expense expense = new Expense();
        expense.setDate(command.getDate());
        expense.setReason(command.getReason());
        expense.setUser(user);
        expense.setSourceCurrencyAmount(currencyAmount);
        expense.setCurrencyAmount(exchangeResult.getTargetAmount());
        expense.setVatData(vatCalculator.calculate(exchangeResult.getTargetAmount().getAmount()));

        return expenseMapper.toDto(expenseRepository.save(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getUserExpenses(long userId) throws UserNotFoundException {

        User user = userService.getUser(userId);

        return expenseRepository.findAllByUserOrderByDateAscCreatedAtAsc(user)
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
    }
}

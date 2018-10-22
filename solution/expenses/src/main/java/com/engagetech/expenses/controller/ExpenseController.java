package com.engagetech.expenses.controller;

import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.dto.VatCalculationDTO;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.CalculateVatCommand;
import com.engagetech.expenses.service.ExpenseNotFoundException;
import com.engagetech.expenses.service.ExpenseProcessException;
import com.engagetech.expenses.service.UserExpenseService;
import com.engagetech.expenses.service.UserNotFoundException;
import com.engagetech.expenses.service.UserService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * API controller that is based UserExpenseService for processing expense requests.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = URL_PREFIX, produces = APPLICATION_JSON_VALUE)
public class ExpenseController {

    private final UserService userService;
    private final UserExpenseService userExpenseService;

    /**
     * Adds single expense, make calculation and returns stored representation.
     *
     * @param command set of values with date, amount (in format 100.00 EUR) and reason of expense
     * @return stored expense with VAT calculation
     */
    @PostMapping
    @Timed
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody AddExpenseCommand command)
            throws ExpenseProcessException {
        final long userId = checkAndGetUserId();
        ExpenseDTO expense = userExpenseService.process(userId, command);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents uriComponents = builder.path(URL_PREFIX + "/{id}")
                .buildAndExpand(expense.getId());
        return ResponseEntity.created(uriComponents.toUri())
                .body(expense);
    }

    /**
     * Gets users expenses. User is currently resolved user by security.
     *
     * @return list of expenses
     */
    @GetMapping
    @Timed
    public ResponseEntity<List<ExpenseDTO>> getUserExpenses() throws UserNotFoundException {
        final long userId = checkAndGetUserId();
        List<ExpenseDTO> userExpenses = userExpenseService.getUserExpenses(userId);
        return ResponseEntity.ok(userExpenses);
    }

    /**
     * Gets single expense. Expense must belong to current user. User is currently resolved user by security.
     *
     * @param id ID of expense
     * @return expense DTO value
     */
    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable long id)
            throws ExpenseNotFoundException, UserNotFoundException, UserExpenseException {
        final long userId = checkAndGetUserId();
        ExpenseDTO userExpense = userExpenseService.getUserExpense(id);
        if (userExpense.getUserId() != userId) {
            throw new UserExpenseException("User is not owner of expense");
        }
        return ResponseEntity.ok(userExpense);
    }


    /**
     * Calculate exchange rate and vat value.
     *
     * @param command input command with date amount (in format 100.00 EUR)
     * @return result of calculation
     * @throws ExpenseProcessException in case of unsupported values.
     */
    @GetMapping("/calculations")
    @Timed
    public ResponseEntity<VatCalculationDTO> calculate(@Valid CalculateVatCommand command)
            throws ExpenseProcessException {
        final VatCalculationDTO vatCalculation = userExpenseService.calculate(command);
        return ResponseEntity.ok(vatCalculation);
    }

    private long checkAndGetUserId() throws UserNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UserNotFoundException("No user");
        }
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("No user");
        }
        Optional<com.engagetech.expenses.model.User> expensesUser =
                userService.getUser(user.getUsername());
        return expensesUser
                .orElseThrow(() -> new UserNotFoundException("No user"))
                .getId();
    }
}

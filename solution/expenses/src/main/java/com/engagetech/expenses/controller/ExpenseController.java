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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = URL_PREFIX, produces = APPLICATION_JSON_VALUE)
public class ExpenseController {

    private final UserService userService;
    private final UserExpenseService userExpenseService;

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

    @GetMapping
    @Timed
    public ResponseEntity<List<ExpenseDTO>> getUserExpenses() throws UserNotFoundException {
        final long userId = checkAndGetUserId();
        List<ExpenseDTO> userExpenses = userExpenseService.getUserExpenses(userId);
        return ResponseEntity.ok(userExpenses);
    }

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

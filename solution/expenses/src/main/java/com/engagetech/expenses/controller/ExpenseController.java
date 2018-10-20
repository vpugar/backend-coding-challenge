package com.engagetech.expenses.controller;


import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.ExpenseProcessException;
import com.engagetech.expenses.service.UserExpenseService;
import com.engagetech.expenses.service.UserNotFoundException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = URL_PREFIX, produces = APPLICATION_JSON_VALUE)
public class ExpenseController {

    private final UserExpenseService userExpenseService;

    @PostMapping
    @Timed
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody AddExpenseCommand command)
            throws ExpenseProcessException {
        // TODO hardcoded userId
        // TODO Check user
        ExpenseDTO expense = userExpenseService.process(1, command);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents uriComponents = builder.path(URL_PREFIX + "/{id}")
                .buildAndExpand(expense.getId());
        return ResponseEntity.created(uriComponents.toUri())
                .body(expense);
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<ExpenseDTO>> getExpenses() throws UserNotFoundException {
        // TODO hardcoded userId
        // TODO Check user
        List<ExpenseDTO> userExpenses = userExpenseService.getUserExpenses(1);
        return ResponseEntity.ok(userExpenses);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable long id) throws ExpenseNotFoundException {
        // TODO Check user
        ExpenseDTO userExpense = userExpenseService.getUserExpense(id);
        return ResponseEntity.ok(userExpense);
    }


    @GetMapping("/calculations")
    @Timed
    public ResponseEntity<VatCalculationDTO> calculate(CalculateVatCommand command)
            throws ExpenseProcessException {
        final VatCalculationDTO vatCalculation = userExpenseService.calculate(command);
        return ResponseEntity.ok(vatCalculation);
    }
}

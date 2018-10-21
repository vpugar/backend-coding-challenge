package com.engagetech.expenses.controller;

import com.engagetech.expenses.dto.CurrencyDTO;
import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.dto.VatCalculationDTO;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.CalculateVatCommand;
import com.engagetech.expenses.service.ExpenseNotFoundException;
import com.engagetech.expenses.service.ExpenseProcessException;
import com.engagetech.expenses.service.UserExpenseService;
import com.engagetech.expenses.service.UserNotFoundException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.APPLICATION_JSON_UTF8;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.USER_ID;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.closeToDouble;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.convertObjectToJsonBytes;
import static com.engagetech.expenses.util.Constants.DATE_INPUT_FORMAT;
import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserExpenseService userExpenseService;

    @Test
    public void given100GBPWhenAddExpenseExpenseThenReturnAmount100() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100");
        command.setDate(today);
        command.setReason("for test 1");

        ExpenseDTO expense = createExpense(today);
        expense.setReason(command.getReason());

        when(userExpenseService.process(USER_ID, command))
                .thenReturn(expense);

        // action && assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, URL_PREFIX + "/" + expense.getId()))
                .andExpect(jsonPath("$.reason", is(command.getReason())))
                .andExpect(jsonPath("$.userId", is((int) expense.getUserId())))
                .andExpect(jsonPath("$.amount", closeToDouble(expense.getAmount())))
                .andExpect(jsonPath("$.vat", closeToDouble(expense.getVatAmount())))
                .andExpect(jsonPath("$.date", is(command.getDate().toString())));
    }

    @Test
    public void givenIllegalArgumentExceptionWhenAddExpenseExpenseThenBadRequest() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        when(userExpenseService.process(USER_ID, command))
                .thenThrow(IllegalArgumentException.class);

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExpenseProcessExceptionWhenAddExpenseExpenseThenBadRequest() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        when(userExpenseService.process(USER_ID, command))
                .thenThrow(ExpenseProcessException.class);

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNotValidAddExpenseCommandWhenAddExpenseExpenseThenBadRequest() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setReason("for test 1");

        when(userExpenseService.process(USER_ID, command))
                .thenThrow(ExpenseProcessException.class);

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserNotFoundExceptionWhenAddExpenseExpenseThenFoebidden() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        when(userExpenseService.process(USER_ID, command))
                .thenThrow(UserNotFoundException.class);

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenRuntimeExceptionExpenseWhenAddExpenseExpenseThenInternalServerError() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        when(userExpenseService.process(USER_ID, command))
                .thenThrow(RuntimeException.class);

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenRequestWhenGetUserExpensesThenExpenseList() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        ExpenseDTO expense1 = createExpense(today);
        expense1.setReason("r1");
        ExpenseDTO expense2 = createExpense(today);
        expense2.setReason("r2");

        when(userExpenseService.getUserExpenses(USER_ID)).thenReturn(
                Arrays.asList(expense1, expense2));

        // action && assert
        mockMvc.perform(get(URL_PREFIX))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reason", is(expense1.getReason())))
                .andExpect(jsonPath("$[0].amount", closeToDouble(expense1.getAmount())))
                .andExpect(jsonPath("$[1].reason", is(expense2.getReason())))
                .andExpect(jsonPath("$[1].date", is(expense2.getDate().toString())));
    }

    @Test
    public void givenUserNotFoundExceptionWhenGetUserExpensesThenForbidden() throws Exception {
        // arrange
        when(userExpenseService.getUserExpenses(USER_ID))
                .thenThrow(UserNotFoundException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenRuntimeExceptionWhenGetUserExpensesThenInternalServerError() throws Exception {
        // arrange
        when(userExpenseService.getUserExpenses(USER_ID))
                .thenThrow(RuntimeException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenUserExpenseWhenGetUserExpenseThenResult() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        ExpenseDTO expense1 = createExpense(today);
        when(userExpenseService.getUserExpense(1L))
                .thenReturn(expense1);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason", is(expense1.getReason())))
                .andExpect(jsonPath("$.userId", is((int) expense1.getUserId())))
                .andExpect(jsonPath("$.amount", closeToDouble(expense1.getAmount())))
                .andExpect(jsonPath("$.vat", closeToDouble(expense1.getVatAmount())))
                .andExpect(jsonPath("$.date", is(expense1.getDate().toString())));
    }

    @Test
    public void givenExpenseNotFoundExceptionWhenGetExpenseThenNotFound() throws Exception {
        // arrange
        when(userExpenseService.getUserExpense(1L))
                .thenThrow(ExpenseNotFoundException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/1"))
                .andExpect(status().isNotFound());
    }

    // TODO need some working
    @Test
    @Ignore
    public void givenUserNotFoundExceptionWhenGetExpenseThenForbidden() throws Exception {
        // arrange
        when(userExpenseService.getUserExpense(1L))
                .thenThrow(UserNotFoundException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenCalculateVatCommandWhenCalculateThenVatCalculation() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount("100 EUR");
        command.setDate(today);

        CurrencyDTO currency = new CurrencyDTO();
        currency.setScale(2);
        currency.setShortName("GBP");

        VatCalculationDTO calculation = new VatCalculationDTO(
                new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("20.00"),
                currency, true
        );
        when(userExpenseService.calculate(command))
                .thenReturn(calculation);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/calculations?date={date}&amount={amount}",
                DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT).format(command.getDate()),
                command.getAmount()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", closeToDouble(calculation.getAmount())))
                .andExpect(jsonPath("$.vatAmount", closeToDouble(calculation.getVatAmount())))
                .andExpect(jsonPath("$.vatRate", closeToDouble(calculation.getVatRate())))
                .andExpect(jsonPath("$.currency.scale", is(calculation.getCurrency().getScale())))
                .andExpect(jsonPath("$.currency.shortName",
                        is(calculation.getCurrency().getShortName())));
    }

    @Test
    public void givenExpenseProcessExceptionWhenCalculateThenBadRequest() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount("100 EUR");
        command.setDate(today);

        when(userExpenseService.calculate(command))
                .thenThrow(ExpenseProcessException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/calculations?date={date}&amount={amount}",
                DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT).format(command.getDate()),
                command.getAmount()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenRuntimeExceptionWhenCalculateThenInternalServerError() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount("100 EUR");
        command.setDate(today);

        when(userExpenseService.calculate(command))
                .thenThrow(RuntimeException.class);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/calculations?date={date}&amount={amount}",
                DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT).format(command.getDate()),
                command.getAmount())
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isInternalServerError());
    }

    private static ExpenseDTO createExpense(LocalDate today) {
        ExpenseDTO expense = new ExpenseDTO();
        expense.setDate(today);
        expense.setReason("this is the reason");
        expense.setAmount(new BigDecimal("100.00"));
        expense.setId(1L);
        expense.setUserId(USER_ID);
        expense.setVatAmount(new BigDecimal("20.00"));
        return expense;
    }
}

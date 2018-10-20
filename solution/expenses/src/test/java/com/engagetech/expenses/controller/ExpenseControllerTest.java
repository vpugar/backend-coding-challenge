package com.engagetech.expenses.controller;

import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.UserExpenseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.AntPathMatcher;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.*;
import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserExpenseService userExpenseService;

    @Test
    public void given100GBPWhenPostExpenseThenReturnAmount100() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100");
        command.setDate(today);
        command.setReason("for test 1");

        ExpenseDTO expense = new ExpenseDTO();
        expense.setDate(today);
        expense.setReason(command.getReason());
        expense.setAmount(new BigDecimal("100.00"));
        expense.setId(1L);
        expense.setUserId(USER_ID);
        expense.setVatAmount(new BigDecimal("20.00"));

        when(userExpenseService.process(USER_ID, command))
                .thenReturn(expense);

        // action && assert
        final MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("date", is(command.getDate())))
                .andExpect(jsonPath("amount", is(expense.getAmount())))
                .andExpect(jsonPath("reason", is(command.getReason())))
                .andExpect(jsonPath("vat", is(expense.getVatAmount())))
                .andExpect(jsonPath("userId", is(expense.getUserId())))
                .andReturn();

        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        AntPathMatcher apm = new AntPathMatcher();
        Long expenseId = Long.valueOf(apm.extractPathWithinPattern(URL_PREFIX + "/*", locationHeader));
        assertThat(expenseId, is(expense.getId()));
    }

    @Test
    public void givenUnknownCurrencyWhenPostExpenseThenAmount100() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 UNW");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenOldExpenseWhenPostExpenseThenAmount100() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 EUR");
        command.setDate(LocalDate.now().minusDays(20));
        command.setReason("for test 1");

        // action / assert
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isBadRequest());
    }


}

package com.engagetech.expenses.controller;

import com.engagetech.expenses.ExpensesApplicationIntegrationTestHelper;
import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.exceptions.GlobalExceptionHandler;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.UserExpenseService;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.exchange.ExchangeCalculatorService;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.AntPathMatcher;
import org.testcontainers.containers.MySQLContainer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.*;
import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ExpensesApplicationIntegrationTestHelper.Initializer.class})
public class ExpenseControllerIntegrationTest {

    @ClassRule
    public static MySQLContainer mysqlSQLContainer = ExpensesApplicationIntegrationTestHelper.getMysqlSQLContainer();

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private MockMvc mockMvc;

    @Autowired
    private UserExpenseService userExpenseService;

    @Autowired
    private ExchangeCalculatorService exchangeCalculatorService;

    @Autowired
    private DefaultCurrency defaultCurrency;

    @Before
    public void prepare() {
        ExpenseController controller = new ExpenseController(userExpenseService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(), controller)
                .build();
    }

    @AfterClass
    public static void destroy() {
        mysqlSQLContainer.close();
    }

    @Test
    public void given100GBPWhenPostExpenseThenAmount100() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100");
        command.setDate(LocalDate.now());
        command.setReason("for test 1");

        // action
        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION)).andReturn();

        // assert
        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        AntPathMatcher apm = new AntPathMatcher();
        Long expenseId = Long.valueOf(apm.extractPathWithinPattern(URL_PREFIX + "/*", locationHeader));

        ExpenseDTO result = userExpenseService.getUserExpense(expenseId);

        errorCollector.checkThat(result.getId(), is(expenseId));
        errorCollector.checkThat(result.getAmount(), is(new BigDecimal("100.00")));
        errorCollector.checkThat(result.getVatAmount(), is(new BigDecimal("20.00")));
        errorCollector.checkThat(result.getDate(), is(command.getDate()));
        errorCollector.checkThat(result.getReason(), is(command.getReason()));
        errorCollector.checkThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void given100EURWhenPostExpenseThenAmountFromExchange() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 EUR");
        command.setDate(LocalDate.now());
        command.setReason("for test 2");

        // action
        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION)).andReturn();

        // assert
        final String locationHeader = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        AntPathMatcher apm = new AntPathMatcher();
        Long expenseId = Long.valueOf(apm.extractPathWithinPattern(URL_PREFIX + "/*", locationHeader));

        ExpenseDTO result = userExpenseService.getUserExpense(expenseId);

        Optional<ExchangeRateDTO> exchangeRate = exchangeCalculatorService.getExchangeRate(command.getDate(),
                "EUR");

        errorCollector.checkThat(result.getId(), is(expenseId));
        errorCollector.checkThat(exchangeRate.isPresent(), is(true));
        final BigDecimal amount = exchangeRate.get().getRate()
                .multiply(new BigDecimal(100))
                .setScale(defaultCurrency.getTarget().getScale(), RoundingMode.HALF_UP);
        errorCollector.checkThat(result.getAmount(), is(amount));
        errorCollector.checkThat(result.getVatAmount(), is(new BigDecimal("20.00")));
        errorCollector.checkThat(result.getDate(), is(command.getDate()));
        errorCollector.checkThat(result.getReason(), is(command.getReason()));
        errorCollector.checkThat(result.getUserId(), is(USER_ID));
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

    // TODO test for getting rate from DB
    // TODO tests for error casses: wrong input, reason too long,
    // TODO check message in error casses
}

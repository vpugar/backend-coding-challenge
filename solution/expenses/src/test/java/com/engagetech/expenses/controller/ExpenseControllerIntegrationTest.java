package com.engagetech.expenses.controller;

import com.engagetech.expenses.ExpensesApplicationIntegrationTestHelper;
import com.engagetech.expenses.IntegrationTest;
import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.exceptions.GlobalExceptionHandler;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.service.AddExpenseCommand;
import com.engagetech.expenses.service.CalculateVatCommand;
import com.engagetech.expenses.service.UserExpenseService;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeCalculatorService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.APPLICATION_JSON_UTF8;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.USER_ID;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.closeToDouble;
import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.convertObjectToJsonBytes;
import static com.engagetech.expenses.util.Constants.DATE_INPUT_FORMAT;
import static com.engagetech.expenses.util.Constants.URL_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
        initializers = ExpensesApplicationIntegrationTestHelper.Initializer.class
)
@Category(IntegrationTest.class)
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

    @SpyBean(name = "FCCApiExchangeCalculator")
    private ExchangeCalculator exchangeCalculator;

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
        errorCollector.checkThat(result.getVatAmount(), is(new BigDecimal("17.64")));
        errorCollector.checkThat(result.getDate(), is(command.getDate()));
        errorCollector.checkThat(result.getReason(), is(command.getReason()));
        errorCollector.checkThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void given100EURFromYesterday2TimesWhenPostExpenseThenAmountFromExchangeAndThenFromDb() throws Exception {
        // arrange
        AddExpenseCommand command = new AddExpenseCommand();
        command.setAmount("100 EUR");
        command.setDate(LocalDate.now().minusDays(1));
        command.setReason("for test 2");

        Optional<ExchangeRateDTO> exchangeRate0 = exchangeCalculatorService.getExchangeRate(command.getDate(),
                "EUR");
        errorCollector.checkThat(exchangeRate0.isPresent(), is(false));

        // action
        MvcResult mvcResult1 = mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION)).andReturn();

        // assert
        verify(exchangeCalculator, only()).calculate(any(LocalDate.class), any(CurrencyAmount.class));
        final String locationHeader = mvcResult1.getResponse().getHeader(HttpHeaders.LOCATION);
        AntPathMatcher apm = new AntPathMatcher();
        Long expenseId = Long.valueOf(apm.extractPathWithinPattern(URL_PREFIX + "/*", locationHeader));

        ExpenseDTO result = userExpenseService.getUserExpense(expenseId);

        Optional<ExchangeRateDTO> exchangeRate1 = exchangeCalculatorService.getExchangeRate(command.getDate(),
                "EUR");

        errorCollector.checkThat(result.getId(), is(expenseId));
        errorCollector.checkThat(exchangeRate1.isPresent(), is(true));
        final BigDecimal amount1 = exchangeRate1.get().getRate()
                .multiply(new BigDecimal(100))
                .setScale(defaultCurrency.getTarget().getScale(), RoundingMode.HALF_UP);
        errorCollector.checkThat(result.getAmount(), is(amount1));
        errorCollector.checkThat(result.getDate(), is(command.getDate()));
        errorCollector.checkThat(result.getReason(), is(command.getReason()));
        errorCollector.checkThat(result.getUserId(), is(USER_ID));

        // action
        MvcResult mvcResult2 = mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION)).andReturn();

        // assert
        verify(exchangeCalculator, only()).calculate(any(LocalDate.class), any(CurrencyAmount.class));
        Optional<ExchangeRateDTO> exchangeRate2 = exchangeCalculatorService.getExchangeRate(command.getDate(),
                "EUR");

        errorCollector.checkThat(result.getId(), is(expenseId));
        errorCollector.checkThat(exchangeRate2.isPresent(), is(true));
        final BigDecimal amount2 = exchangeRate2.get().getRate()
                .multiply(new BigDecimal(100))
                .setScale(defaultCurrency.getTarget().getScale(), RoundingMode.HALF_UP);
        errorCollector.checkThat(result.getAmount(), is(amount2));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("UnknownCurrencyException")))
                .andExpect(jsonPath("$.message", is("Currency UNW not found")));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("DateOutOfBoundsException")))
                .andExpect(jsonPath("$.message", is("Date is before allowed date")));
    }

    @Test
    public void givenUserWhenGetUserExpensesThenExpenses() throws Exception {
        // arrange
        AddExpenseCommand command1 = new AddExpenseCommand();
        command1.setAmount("100");
        command1.setDate(LocalDate.now());
        command1.setReason("for test 1");
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command1)))
                .andExpect(status().isCreated());

        AddExpenseCommand command2 = new AddExpenseCommand();
        command2.setAmount("100");
        command2.setDate(LocalDate.now());
        command2.setReason("for test 2");
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command2)))
                .andExpect(status().isCreated());

        List<ExpenseDTO> userExpenses = userExpenseService.getUserExpenses(USER_ID);
        ExpenseDTO expense1 = userExpenses.get(0);
        ExpenseDTO expense2 = userExpenses.get(0);

        // action && assert
        mockMvc.perform(get(URL_PREFIX))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(userExpenses.size())))
                .andExpect(jsonPath("$[0].reason", is(expense1.getReason())))
                .andExpect(jsonPath("$[0].amount", closeToDouble(expense1.getAmount())))
                .andExpect(jsonPath("$[1].reason", is(expense2.getReason())))
                .andExpect(jsonPath("$[1].vat", closeToDouble(expense2.getVatAmount())))
                .andExpect(jsonPath("$[1].date", is(expense2.getDate().toString())));
    }

    @Test
    public void givenUserExpenseIdWhenGetExpenseThenExpense() throws Exception {
        // arrange
        AddExpenseCommand command1 = new AddExpenseCommand();
        command1.setAmount("100");
        command1.setDate(LocalDate.now());
        command1.setReason("for test 1");
        mockMvc.perform(post(URL_PREFIX)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(command1)))
                .andExpect(status().isCreated());

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason", is(command1.getReason())))
                .andExpect(jsonPath("$.amount", closeToDouble(new BigDecimal("100"))));
    }

    @Test
    public void givenDateAndCurrencyWhenCalculateThenVatCalculation() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount("100 EUR");
        command.setDate(today);

        // action / assert
        mockMvc.perform(get(URL_PREFIX + "/calculations?date={date}&amount={amount}",
                DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT).format(command.getDate()),
                command.getAmount()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", notNullValue()))
                .andExpect(jsonPath("$.vatAmount", notNullValue()))
                .andExpect(jsonPath("$.vatRate", notNullValue()))
                .andExpect(jsonPath("$.currency.scale", is(defaultCurrency.getTarget().getScale())))
                .andExpect(jsonPath("$.currency.shortName", is(defaultCurrency.getTarget().getShortName())));
    }

    // TODO security for expense, expenses, addexpense

}

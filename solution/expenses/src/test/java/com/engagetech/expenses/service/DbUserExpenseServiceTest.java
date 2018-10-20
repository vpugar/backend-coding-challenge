package com.engagetech.expenses.service;

import com.engagetech.expenses.TestCurrencies;
import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.dto.VatCalculationDTO;
import com.engagetech.expenses.mapper.CurrencyMapper;
import com.engagetech.expenses.mapper.CurrencyMapperImpl;
import com.engagetech.expenses.mapper.ExpenseMapper;
import com.engagetech.expenses.mapper.ExpenseMapperImpl;
import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.User;
import com.engagetech.expenses.model.VatData;
import com.engagetech.expenses.repository.ExpenseRepository;
import com.engagetech.expenses.service.currency.CurrencyAmountParser;
import com.engagetech.expenses.service.exchange.ExchangeCalculator;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import com.engagetech.expenses.service.vat.VatCalculator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.engagetech.expenses.ExpensesApplicationJunitTestHelper.USER_ID;
import static com.engagetech.expenses.TestCurrencies.EUR_GBP_RATE;
import static com.engagetech.expenses.TestCurrencies.eurCurrency;
import static com.engagetech.expenses.TestCurrencies.gbpCurrency;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DbUserExpenseServiceTest {

    private static final long EXPENSE_ID = 1L;
    private static final String INPUT_AMOUNT = "100 EUR";
    private static final BigDecimal OUTPUT_AMOUNT = new BigDecimal("88.15");
    private static final BigDecimal VAT_AMOUNT = new BigDecimal("17.63");
    private static final CurrencyAmount currencyAmount = new CurrencyAmount(eurCurrency, new BigDecimal("100.00"));
    private static final VatData vatData = new VatData(new BigDecimal("20.00"), new BigDecimal("20.00"));

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final ExpenseMapper expenseMapper = new ExpenseMapperImpl();
    private final CurrencyMapper currencyMapper = new CurrencyMapperImpl();
    private final ExpenseDatePolicy expenseDatePolicy = new PastExpenseDatePolicy(10);

    private DbUserExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserService userService;
    @Mock
    private CurrencyAmountParser currencyAmountParser;
    @Mock
    private ExchangeCalculator exchangeCalculator;
    @Mock
    private VatCalculator vatCalculator;

    @Before
    public void prepare() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(currencyAmountParser.parse(INPUT_AMOUNT))
                .thenReturn(currencyAmount);
        when(vatCalculator.calculate(OUTPUT_AMOUNT))
                .thenReturn(vatData);
        final User user = new User();
        user.setId(USER_ID);
        when(userService.getUser(USER_ID))
                .thenReturn(user);
        when(expenseRepository.save(anyObject()))
                .thenAnswer((Answer<Expense>) invocation -> {
                    Object[] args = invocation.getArguments();
                    Expense expense = (Expense) args[0];
                    expense.setId(EXPENSE_ID);
                    return expense;
                });

        expenseService = new DbUserExpenseService(
                expenseRepository, expenseMapper, currencyMapper, userService, currencyAmountParser,
                expenseDatePolicy, exchangeCalculator, vatCalculator
        );
    }

    @Test
    public void givenAddExpenseCommandWhenProcessThenMatchingExpense() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        AddExpenseCommand addExpenseCommand = new AddExpenseCommand();
        addExpenseCommand.setAmount(INPUT_AMOUNT);
        addExpenseCommand.setDate(today);
        addExpenseCommand.setReason("simple");

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        when(exchangeCalculator.calculate(addExpenseCommand.getDate(), currencyAmount))
                .thenReturn(Optional.of(new ExchangeResult(today, EUR_GBP_RATE, currencyAmount, gbpCurrency)));

        // action
        ExpenseDTO result = expenseService.process(USER_ID, addExpenseCommand);

        // assert
        verify(expenseRepository).save(expenseCaptor.capture());
        Expense expense = expenseCaptor.getValue();
        errorCollector.checkThat(expense.getId(), is(EXPENSE_ID));
        errorCollector.checkThat(expense.getCreatedAt(), notNullValue());
        errorCollector.checkThat(expense.getCurrencyAmount().getAmount(), is(OUTPUT_AMOUNT));
        errorCollector.checkThat(expense.getCurrencyAmount().getCurrency(), is(gbpCurrency));
        errorCollector.checkThat(expense.getDate(), is(addExpenseCommand.getDate()));
        errorCollector.checkThat(expense.getReason(), is(addExpenseCommand.getReason()));
        errorCollector.checkThat(expense.getSourceCurrencyAmount(), notNullValue());
        errorCollector.checkThat(expense.getVatData(), is(vatData));
        errorCollector.checkThat(expense.getUser(), notNullValue());

        errorCollector.checkThat(result.getId(), is(EXPENSE_ID));
        errorCollector.checkThat(result.getAmount(), is(OUTPUT_AMOUNT));
        errorCollector.checkThat(result.getVatAmount(), is(new BigDecimal("20.00")));
        errorCollector.checkThat(result.getDate(), is(addExpenseCommand.getDate()));
        errorCollector.checkThat(result.getReason(), is(addExpenseCommand.getReason()));
        errorCollector.checkThat(result.getUserId(), is(USER_ID));
    }

    @Test
    public void givenAddExpenseCommandWithoutExchangeResultWhenProcessThenExchangeProcessException() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        AddExpenseCommand addExpenseCommand = new AddExpenseCommand();
        addExpenseCommand.setAmount(INPUT_AMOUNT);
        addExpenseCommand.setDate(today);
        addExpenseCommand.setReason("simple");

        when(exchangeCalculator.calculate(addExpenseCommand.getDate(), currencyAmount))
                .thenReturn(Optional.empty());

        // assert
        expectedException.expect(ExchangeProcessException.class);
        expectedException.expectMessage("Cannot calculate exchange for");

        // action
        expenseService.process(USER_ID, addExpenseCommand);
    }

    @Test
    public void givenCalculateVatCommandWhenCalculateThenVatCalculation() throws Exception {
        // arrange
        Currency targetCurrency = gbpCurrency;
        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount(INPUT_AMOUNT);
        command.setDate(LocalDate.now());

        when(exchangeCalculator.calculate(command.getDate(), currencyAmount))
                .thenReturn(Optional.of(new ExchangeResult(command.getDate(), EUR_GBP_RATE,
                        currencyAmount, targetCurrency)));

        // action
        VatCalculationDTO result = expenseService.calculate(command);

        // assert
        errorCollector.checkThat(result.getAmount(), is(OUTPUT_AMOUNT));
        errorCollector.checkThat(result.getCurrency(), is(currencyMapper.toDto(targetCurrency)));
        errorCollector.checkThat(result.getVatAmount(), is(vatData.getVatAmount()));
        errorCollector.checkThat(result.getVatRate(), is(vatData.getVatRate()));
    }

    @Test
    public void givenNoExchangeDataWhenCalculateThenExchangeProcessException() throws Exception {
        // arrange
        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount(INPUT_AMOUNT);
        command.setDate(LocalDate.now());

        when(exchangeCalculator.calculate(command.getDate(), currencyAmount))
                .thenReturn(Optional.empty());

        // assert
        expectedException.expect(ExchangeProcessException.class);
        expectedException.expectMessage("Cannot calculate exchange for " +
                currencyAmount.getCurrency());

        // action
        expenseService.calculate(command);
    }

    @Test
    public void givenCalculateVatCommandWithoutDateWhenCalculateThenVatCalculationWithCurrentDate() throws Exception {
        // arrange
        Currency targetCurrency = gbpCurrency;
        final LocalDate today = LocalDate.now();
        CalculateVatCommand command = new CalculateVatCommand();
        command.setAmount(INPUT_AMOUNT);

        when(exchangeCalculator.calculate(today, currencyAmount))
                .thenReturn(Optional.of(new ExchangeResult(command.getDate(), EUR_GBP_RATE,
                        currencyAmount, targetCurrency)));

        // action
        VatCalculationDTO result = expenseService.calculate(command);

        // assert
        errorCollector.checkThat(result.getAmount(), is(OUTPUT_AMOUNT));
        errorCollector.checkThat(result.getCurrency(), is(currencyMapper.toDto(targetCurrency)));
        errorCollector.checkThat(result.getVatAmount(), is(vatData.getVatAmount()));
        errorCollector.checkThat(result.getVatRate(), is(vatData.getVatRate()));
    }

    @Test
    public void givenExpensesForUserWhenGetUserExpensesThenReturnExpensesInTargetCurrencyWithVat() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CurrencyAmount srcCurrencyAmount = new CurrencyAmount(
                TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        CurrencyAmount destCurrencyAmount = new CurrencyAmount(
                TestCurrencies.gbpCurrency, new BigDecimal("100.00"));

        User user = new User();
        user.setEmail("email@test.com");
        user.setUsername("test");
        user.setId(USER_ID);

        Expense expense1 = new Expense();
        expense1.setDate(today);
        expense1.setSourceCurrencyAmount(srcCurrencyAmount);
        expense1.setCurrencyAmount(destCurrencyAmount);
        expense1.setVatData(vatData);
        expense1.setReason("reason1");
        expense1.setUser(user);

        Expense expense2 = new Expense();
        expense2.setDate(today);
        expense2.setSourceCurrencyAmount(srcCurrencyAmount);
        expense2.setCurrencyAmount(destCurrencyAmount);
        expense2.setVatData(vatData);
        expense2.setReason("reason2");
        expense2.setUser(user);

        when(expenseRepository.findAllByUserOrderByDateAscCreatedAtAsc(user)).thenReturn(
                Stream.of(expense1, expense2));
        when(userService.getUser(USER_ID)).thenReturn(user);

        // action
        List<ExpenseDTO> userExpenses = expenseService.getUserExpenses(USER_ID);

        // assert
        errorCollector.checkThat(userExpenses.size(), is(2));
        ExpenseDTO expenseDTO1 = userExpenses.get(0);
        errorCollector.checkThat(expenseDTO1.getDate(), is(today));
        errorCollector.checkThat(expenseDTO1.getVatAmount(), is(vatData.getVatAmount()));
        errorCollector.checkThat(expenseDTO1.getAmount(), is(destCurrencyAmount.getAmount()));
        errorCollector.checkThat(expenseDTO1.getReason(), is(expense1.getReason()));
        errorCollector.checkThat(expenseDTO1.getUserId(), is(USER_ID));
        errorCollector.checkThat(userExpenses.get(1).getReason(), is(expense2.getReason()));
    }

    @Test
    public void givenExpenseInDbWhenGetUserExpenseThenExpenseDTO() throws Exception {
        // arrange
        final LocalDate today = LocalDate.now();

        CurrencyAmount srcCurrencyAmount = new CurrencyAmount(
                TestCurrencies.eurCurrency, new BigDecimal("100.00"));
        CurrencyAmount destCurrencyAmount = new CurrencyAmount(
                TestCurrencies.gbpCurrency, new BigDecimal("100.00"));

        User user = new User();
        user.setEmail("email@test.com");
        user.setUsername("test");
        user.setId(USER_ID);

        Expense expense = new Expense();
        expense.setDate(today);
        expense.setSourceCurrencyAmount(srcCurrencyAmount);
        expense.setCurrencyAmount(destCurrencyAmount);
        expense.setVatData(vatData);
        expense.setReason("reason1");
        expense.setUser(user);
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.of(expense));

        // action
        ExpenseDTO expenseDTO = expenseService.getUserExpense(EXPENSE_ID);

        // assert
        errorCollector.checkThat(expenseDTO.getDate(), is(today));
        errorCollector.checkThat(expenseDTO.getVatAmount(), is(vatData.getVatAmount()));
        errorCollector.checkThat(expenseDTO.getAmount(), is(destCurrencyAmount.getAmount()));
        errorCollector.checkThat(expenseDTO.getReason(), is(expense.getReason()));
        errorCollector.checkThat(expenseDTO.getUserId(), is(USER_ID));
    }

    @Test
    public void givenNoExpenseWhenGetUserExpenseThenExpenseNotFoundException() throws Exception {
        // arrange
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.empty());

        // assert
        expectedException.expect(ExpenseNotFoundException.class);
        expectedException.expectMessage("Not found expense with id " + EXPENSE_ID);

        // action
        ExpenseDTO expense = expenseService.getUserExpense(EXPENSE_ID);
    }
}

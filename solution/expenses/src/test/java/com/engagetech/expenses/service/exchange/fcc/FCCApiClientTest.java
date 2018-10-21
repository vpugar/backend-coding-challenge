package com.engagetech.expenses.service.exchange.fcc;


import com.engagetech.expenses.IntegrationTest;
import com.engagetech.expenses.model.Currency;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {
                "app.expense.exchange.fcc-api.api-url=https://free.currencyconverterapi.com/api/v6"
        },
        classes = FCCApiConfiguration.class
)
@Category(IntegrationTest.class)
public class FCCApiClientTest {

    private static final Currency gbpCurrency = new Currency("GBP", 2);
    private static final Currency eurCurrency = new Currency("EUR", 2);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private FCCApiClient fccApiClient;

    @Test
    public void givenValidPairWhenConvertThenRateIsPresent() {
        // action
        Optional<BigDecimal> rateOptional = fccApiClient.convert(LocalDate.now(), gbpCurrency, eurCurrency);

        // assert
        assertTrue(rateOptional.isPresent());
        assertThat(rateOptional.get(), greaterThan(BigDecimal.ZERO));
    }

    @Test
    public void givenInvalidPairWhenConvertThenRateIsPresent() {
        // assert
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(startsWith("status 400 reading"));

        // action
        fccApiClient.convert(LocalDate.now(),
                new Currency("AAA", 0),
                new Currency("BBB", 0));
    }
}

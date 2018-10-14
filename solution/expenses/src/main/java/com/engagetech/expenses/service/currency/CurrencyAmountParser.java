package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class CurrencyAmountParser {

    private final DefaultCurrency defaultCurrency;
    private final CurrencyService currencyService;

    public CurrencyAmount parse(String amountWithCurrency) throws NoAmountException, UnknownCurrencyException {

        if (StringUtils.isBlank(amountWithCurrency)) {
            throw new NoAmountException("amountWithCurrency is blank");
        }

        String[] split = amountWithCurrency.trim().split(" ");

        if (split.length == 1) {
            if (NumberUtils.isCreatable(split[0])) {
                Currency currency = defaultCurrency.getTarget();
                return new CurrencyAmount(
                        currency,
                        new BigDecimal(split[0]).setScale(currency.getScale(), RoundingMode.HALF_UP));
            }
        } else {
            // TODO check length
            int amountPosition = positionOfAmount(split);
            int currencyPosition = (amountPosition + 1) % 2;
            Currency currency = currencyService.getCurrency(split[currencyPosition]);

            if (!defaultCurrency.getSupported().contains(currency)) {
                throw new UnknownCurrencyException("Not supported currency " + currency.getShortName());
            }

            // TODO Check if needed to throw exception in case of wrong input scale
            return new CurrencyAmount(
                    currency,
                    new BigDecimal(split[amountPosition]).setScale(currency.getScale(), RoundingMode.HALF_UP));
        }

        throw new NoAmountException("Cannot parse amount");
    }

    private static int positionOfAmount(String[] amountWithCurrency) throws NoAmountException {
        for (int position = 0; position < amountWithCurrency.length; position++) {
            if (NumberUtils.isCreatable(amountWithCurrency[position])) {
                return position;
            }
        }
        throw new NoAmountException("No amount in expression");
    }
}

package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Service that stores target currency and supported currencies.
 * Target is one (configuration with app.expense.default-currency.target-currency.short-name) that will be used for
 * storing all expenses.
 * Supported currencies (configuration with with app.expense.default-currency.supported-currencies.short-name)
 * is set of currencies that can be used for converting with exchange service.
 */
@Service
public class DefaultCurrency {

    private final CurrencyService currencyService;

    private final String targetCurrencyShortName;
    private final Set<String> supportedCurrenciesShortName;

    private AtomicReference<Currency> targetCurrency;
    private AtomicReference<Set<Currency>> supportedCurrencies;

    public DefaultCurrency(
            CurrencyService currencyService,
            @Value("${app.expense.default-currency.target-currency.short-name}")
            @NonNull String targetCurrencyShortName,
            @Value("${app.expense.default-currency.supported-currencies.short-name}")
            @NonNull String[] supportedCurrenciesShortName) {
        this.currencyService = currencyService;
        this.targetCurrencyShortName = targetCurrencyShortName;
        this.supportedCurrenciesShortName = new HashSet<>(Arrays.asList(supportedCurrenciesShortName));
        this.supportedCurrenciesShortName.add(this.targetCurrencyShortName);
    }

    @PostConstruct
    public void init() throws UnknownCurrencyException {

        targetCurrency = new AtomicReference<>(currencyService.getCurrency(targetCurrencyShortName)
                .clone());

        try {
            Set<Currency> currencies = supportedCurrenciesShortName.stream()
                    .map(this::mapShortNameToCurrency)
                    .collect(Collectors.toSet());
            supportedCurrencies = new AtomicReference<>(Collections.unmodifiableSet(currencies));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof UnknownCurrencyException) {
                throw (UnknownCurrencyException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    public Currency getTarget() {
        return targetCurrency.get().clone();
    }

    public Set<Currency> getSupported() {
        return supportedCurrencies.get();
    }

    private Currency mapShortNameToCurrency(String shortName) {
        try {
            return currencyService.getCurrency(shortName)
                    .clone();
        } catch (UnknownCurrencyException e) {
            throw new RuntimeException(e);
        }
    }

}

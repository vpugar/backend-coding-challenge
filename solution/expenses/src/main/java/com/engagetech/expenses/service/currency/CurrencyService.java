package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;

/**
 * Service for getting currencies from storage.
 */
public interface CurrencyService {

    Currency getCurrency(String shortName) throws UnknownCurrencyException;

}

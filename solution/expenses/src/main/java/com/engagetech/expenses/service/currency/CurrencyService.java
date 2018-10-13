package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.service.currency.UnknownCurrencyException;

public interface CurrencyService {

    Currency getCurrency(String shortName) throws UnknownCurrencyException;

}

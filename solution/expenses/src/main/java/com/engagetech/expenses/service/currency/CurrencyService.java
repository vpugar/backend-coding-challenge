package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;

public interface CurrencyService {

    Currency getCurrency(String shortName) throws UnknownCurrencyException;

}

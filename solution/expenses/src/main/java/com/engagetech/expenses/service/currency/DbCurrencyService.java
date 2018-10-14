package com.engagetech.expenses.service.currency;

import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.repository.CurrencyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DbCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public Currency getCurrency(@NonNull String shortName) throws UnknownCurrencyException {
        return currencyRepository.findByShortName(shortName)
              .orElseThrow(() -> new UnknownCurrencyException("Currency " + shortName + " not found"));
    }

}

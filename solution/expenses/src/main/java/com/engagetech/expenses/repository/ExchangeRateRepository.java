package com.engagetech.expenses.repository;


import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.ExchangeRate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByDateAndSourceCurrencyAndTargetCurrency(LocalDate date, Currency sourceCurrency,
                                                                        Currency targetCurrency);


}

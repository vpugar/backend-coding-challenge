package com.engagetech.expenses.repository;

import com.engagetech.expenses.model.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Long> {

    Optional<Currency> findByShortName(String shortName);

}

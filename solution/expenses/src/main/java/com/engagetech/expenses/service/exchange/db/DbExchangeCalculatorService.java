package com.engagetech.expenses.service.exchange.db;

import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.mapper.ExchangeRateMapper;
import com.engagetech.expenses.model.Currency;
import com.engagetech.expenses.model.CurrencyAmount;
import com.engagetech.expenses.model.ExchangeRate;
import com.engagetech.expenses.repository.ExchangeRateRepository;
import com.engagetech.expenses.service.currency.CurrencyService;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import com.engagetech.expenses.service.currency.UnknownCurrencyException;
import com.engagetech.expenses.service.exchange.ExchangeCalculatorService;
import com.engagetech.expenses.service.exchange.ExchangeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DbExchangeCalculatorService implements ExchangeCalculatorService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;
    private final DefaultCurrency defaultCurrency;
    private final CurrencyService currencyService;

    @Override
    @Transactional(readOnly = true)
    public Optional<ExchangeResult> calculate(LocalDate date, CurrencyAmount sourceCurrencyAmount) {

        Currency targetCurrency = defaultCurrency.getTarget();

        if (targetCurrency.equals(sourceCurrencyAmount.getCurrency())) {
            return Optional.of(new ExchangeResult(date, BigDecimal.ONE, sourceCurrencyAmount, targetCurrency));
        }

        Optional<ExchangeRate> exchangeRateOptional =
                exchangeRateRepository.findByDateAndSourceCurrencyAndTargetCurrency(
                        date, sourceCurrencyAmount.getCurrency(), targetCurrency);

        return exchangeRateOptional
                .map(exchangeRate ->
                        new ExchangeResult(
                                date,
                                exchangeRate.getRate(),
                                sourceCurrencyAmount,
                                targetCurrency
                        ));
    }

    @Transactional
    public void saveResult(ExchangeResult exchangeResult) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setDate(exchangeResult.getDate());
        exchangeRate.setRate(exchangeResult.getRate());
        exchangeRate.setSourceCurrency(exchangeResult.getSourceAmount().getCurrency());
        exchangeRate.setTargetCurrency(exchangeResult.getTargetAmount().getCurrency());
        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExchangeRateDTO> getExchangeRate(LocalDate date, String sourceCurrencyShortName)
            throws UnknownCurrencyException {

        Currency targetCurrency = defaultCurrency.getTarget();
        Currency sourceCurrency = currencyService.getCurrency(sourceCurrencyShortName);

        Optional<ExchangeRate> exchangeRateOptional =
                exchangeRateRepository.findByDateAndSourceCurrencyAndTargetCurrency(
                        date, sourceCurrency, targetCurrency);
        return exchangeRateOptional
                .map(exchangeRateMapper::toDto);
    }

}

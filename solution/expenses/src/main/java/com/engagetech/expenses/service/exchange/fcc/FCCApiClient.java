package com.engagetech.expenses.service.exchange.fcc;

import com.engagetech.expenses.model.Currency;
import feign.Param;
import feign.RequestLine;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;

/**
 * The Free Currency Converter API: free.currencyconverterapi.com
 * <p>
 * URL example: https://free.currencyconverterapi.com/api/v6/convert?q=USD_EUR,EUR_USD&compact=ultra&date=2018-10-14
 */
public interface FCCApiClient {

    class Helper {

        static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        static String formatCurrencyPair(Currency source, Currency target) {
            return source.getShortName() + "_" + target.getShortName();
        }
    }

    class CurrencyPair extends HashMap<String, DateRate> {

        private static final long serialVersionUID = 2961950924898716717L;

        @Override
        public CurrencyPair clone() {
            return (CurrencyPair) super.clone();
        }
    }

    class DateRate extends HashMap<String, BigDecimal> {

        private static final long serialVersionUID = -939726655176591901L;

        @Override
        public DateRate clone() {
            return (DateRate) super.clone();
        }
    }

    @RequestLine("GET /convert?q={q}&compact=ultra&date={date}")
    CurrencyPair convertApi(@Param("date") String date, @Param("q") String pairs);

    default Optional<BigDecimal> convert(LocalDate date, Currency source, Currency target) {
        String currencyPair = Helper.formatCurrencyPair(source, target);
        final String formattedDate = Helper.DATE_TIME_FORMATTER.format(date);
        CurrencyPair pair = convertApi(
                formattedDate, StringUtils.join(new String[]{currencyPair}, ','));

        DateRate dateRate = pair.get(currencyPair);
        if (dateRate != null) {
            return Optional.ofNullable(dateRate.get(formattedDate));
        }
        return Optional.empty();
    }
}

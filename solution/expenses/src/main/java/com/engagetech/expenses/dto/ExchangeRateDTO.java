package com.engagetech.expenses.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExchangeRateDTO {

    @JsonIgnore
    private long id;

    private long sourceCurrencyId;

    private long targetCurrencyId;

    @NotNull
    private LocalDate date;

    @NotNull
    private BigDecimal rate;
}

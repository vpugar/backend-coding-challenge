package com.engagetech.expenses.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.engagetech.expenses.util.Constants.DATE_OUTPUT_FORMAT;

@Data
public class ExchangeRateDTO {

    @JsonIgnore
    private long id;

    private long sourceCurrencyId;

    private long targetCurrencyId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_OUTPUT_FORMAT)
    private LocalDate date;

    @NotNull
    private BigDecimal rate;
}

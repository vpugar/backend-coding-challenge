package com.engagetech.expenses.dto;

import com.engagetech.expenses.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.engagetech.expenses.util.Constants.DATE_OUTPUT_FORMAT;
import static com.engagetech.expenses.util.Constants.STRING_MAX_LENGTH;

@Data
public class ExpenseDTO {

    @JsonIgnore
    private long id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_OUTPUT_FORMAT)
    private LocalDate date;

    @NotNull
    private BigDecimal amount;

    @Length(min = 1, max = STRING_MAX_LENGTH)
    private String reason;

    @NotNull
    @JsonProperty("vat")
    private BigDecimal vatAmount;

    private long userId;

}

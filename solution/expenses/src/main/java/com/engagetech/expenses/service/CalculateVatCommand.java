package com.engagetech.expenses.service;

import com.engagetech.expenses.util.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CalculateVatCommand {

    @DateTimeFormat(pattern = Constants.DATE_INPUT_FORMAT)
    private LocalDate date;

    @NotNull
    private String amount;

}

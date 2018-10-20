package com.engagetech.expenses.service;


import com.engagetech.expenses.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class CalculateVatCommand {

    @DateTimeFormat(pattern = Constants.DATE_INPUT_FORMAT)
    private LocalDate date;

    @NotNull
    private String amount;

}

package com.engagetech.expenses.service;


import com.engagetech.expenses.util.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class AddExpenseCommand {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_INPUT_FORMAT)
    private LocalDate date;

    @NotNull
    private String amount;

    @Length(min = 1, max = Constants.STRING_MAX_LENGTH)
    private String reason;

}

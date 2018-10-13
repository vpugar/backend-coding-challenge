package com.engagetech.expenses.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CurrencyDTO {

    @JsonIgnore
    private long id;

    private String shortName;

    private int scale;
}

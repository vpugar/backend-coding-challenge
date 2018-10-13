package com.engagetech.expenses.dto;

import com.engagetech.expenses.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDTO {

    @JsonIgnore
    private long id;

    @Length(min = 5, max = Constants.STRING_MAX_LENGTH)
    @NotNull
    private String username;

    @Email
    @NotNull
    private String email;

}

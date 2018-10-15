package com.engagetech.expenses.mapper;

import com.engagetech.expenses.dto.ExpenseDTO;
import com.engagetech.expenses.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.engagetech.expenses.util.Constants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, uses = {UserMapper.class, PrimaryKeyMapper.class})
public interface ExpenseMapper extends EntityMapper<ExpenseDTO, Expense> {

    @Override
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "currencyAmount.amount", target = "amount")
    @Mapping(source = "vatData.vatAmount", target = "vatAmount")
    ExpenseDTO toDto(Expense entity);

    @Override
    @Mapping(source = "userId", target = "user")
    @Mapping(target = "sourceCurrencyAmount", ignore = true)
    @Mapping(target = "currencyAmount", ignore = true)
    @Mapping(target = "vatData", ignore = true)
    Expense toEntity(ExpenseDTO dto);

}

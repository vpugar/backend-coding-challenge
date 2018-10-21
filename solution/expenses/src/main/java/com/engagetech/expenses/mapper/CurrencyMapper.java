package com.engagetech.expenses.mapper;

import com.engagetech.expenses.dto.CurrencyDTO;
import com.engagetech.expenses.model.Currency;
import org.mapstruct.Mapper;

import static com.engagetech.expenses.util.Constants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, uses = {PrimaryKeyMapper.class})
public interface CurrencyMapper extends EntityMapper<CurrencyDTO, Currency> {

    @Override
    CurrencyDTO toDto(Currency entity);

}

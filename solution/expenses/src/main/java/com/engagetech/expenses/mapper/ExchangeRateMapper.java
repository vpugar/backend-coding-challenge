package com.engagetech.expenses.mapper;

import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.engagetech.expenses.util.Constants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, uses = {PrimaryKeyMapper.class})
public interface ExchangeRateMapper extends EntityMapper<ExchangeRateDTO, ExchangeRate> {

    @Override
    @Mapping(source = "sourceCurrency.id", target = "sourceCurrencyId")
    @Mapping(source = "targetCurrency.id", target = "targetCurrencyId")
    ExchangeRateDTO toDto(ExchangeRate entity);

    @Override
    @Mapping(source = "sourceCurrencyId", target = "sourceCurrency")
    @Mapping(source = "targetCurrencyId", target = "targetCurrency")
    ExchangeRate toEntity(ExchangeRateDTO dto);

}

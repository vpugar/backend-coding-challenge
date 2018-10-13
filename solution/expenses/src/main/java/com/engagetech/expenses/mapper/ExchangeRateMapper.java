package com.engagetech.expenses.mapper;

import com.engagetech.expenses.dto.ExchangeRateDTO;
import com.engagetech.expenses.model.ExchangeRate;
import org.mapstruct.Mapper;

import static com.engagetech.expenses.util.Constants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL, uses = {PrimaryKeyMapper.class})
public interface ExchangeRateMapper extends EntityMapper<ExchangeRateDTO, ExchangeRate> {

}

package com.engagetech.expenses.mapper;

import com.engagetech.expenses.dto.UserDTO;
import com.engagetech.expenses.model.User;
import org.mapstruct.Mapper;

import static com.engagetech.expenses.util.Constants.MAPPER_COMPONENT_MODEL;

@Mapper(componentModel = MAPPER_COMPONENT_MODEL)
public interface UserMapper extends EntityMapper<UserDTO, User> {

}

package org.example.someservice.mapper;

import org.example.someservice.dto.UserDto;
import org.example.someservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(User user);
}

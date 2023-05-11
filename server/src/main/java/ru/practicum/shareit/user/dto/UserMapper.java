package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(User user);

    User map(UserDto userDto);

    @Mapping(target = "id", expression = "java(userId)")
    @Mapping(target = "email", expression = "java(userDto.getEmail() == null ? userFromDB.getEmail() : userDto.getEmail())")
    @Mapping(target = "name", expression = "java(userDto.getName() == null ? userFromDB.getName() : userDto.getName())")
    User merge(Long userId, @MappingTarget User userFromDB, UserDto userDto);

}

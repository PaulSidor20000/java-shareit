package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto read(Long userId);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    Collection<UserDto> findAll();
}

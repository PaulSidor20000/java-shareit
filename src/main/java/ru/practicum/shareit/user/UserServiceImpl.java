package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    public UserDto create(UserDto userDto) {
        User user = userDtoMapper.mapToNewUser(userDto);
        return userDtoMapper.mapToUserDto(userStorage.create(user));
    }

    public UserDto read(Long userId) {
        return userDtoMapper.mapToUserDto(userStorage.read(userId));
    }

    public UserDto update(Long userId, UserDto userDto) {
        User user = userDtoMapper.mapToUserModel(userId, userDto);
        return userDtoMapper.mapToUserDto(userStorage.update(user));
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(userDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}

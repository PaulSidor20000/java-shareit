package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    private final UserStorage userStorage;

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User mapToUserModel(UserDto userDto) {
        return userStorage.read(userDto.getId());
    }

}

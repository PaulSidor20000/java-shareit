package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;

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

    public User mapToUserModel(Long userId, UserDto userDto) {
        User user = userStorage.read(userDto.getId());
        return User.builder()
                .id(userId)
                .email(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail())
                .name(userDto.getName() == null ? user.getName() : userDto.getName())
                .items(user.getItems())
                .build();
    }

    public User mapToNewUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .items(Collections.emptyList())
                .build();
    }

}

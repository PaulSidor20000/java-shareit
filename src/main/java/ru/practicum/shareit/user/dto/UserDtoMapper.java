package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.Optional;

import static ru.practicum.shareit.exceptions.ErrorHandler.ENTITY_NOT_FOUND_MESSAGE;

@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    private final UserStorage userStorage;

    public UserDto mapToUserDto(Optional<User> aUser) {
        User user = aUser.orElseThrow(() ->
                new EntityNotFoundException(String.format(ENTITY_NOT_FOUND_MESSAGE, (Object) null))
        );
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User mapToUserModel(Long userId, UserDto userDto) {
        Optional<User> aUser = userStorage.findById(userId);
        User user = null;

        if (aUser.isPresent()) {
            user = aUser.get();
        }

        return User.builder()
                .id(userId)
                .email(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail())
                .name(userDto.getName() == null ? user.getName() : userDto.getName())
                .itemIds(user.getItemIds())
                .build();
    }

    public User mapToNewUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .itemIds(Collections.emptySet())
                .build();
    }

}

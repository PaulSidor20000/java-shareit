package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.function.Function;

import static ru.practicum.shareit.exceptions.ErrorHandler.ENTITY_NOT_FOUND_MESSAGE;

@Component
@RequiredArgsConstructor
public class UserDtoMapper implements Function<User, UserDto> {
    private final UserStorage userStorage;

    @Override
    public UserDto apply(User user) {
        return null;
    }

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

        return aUser.map(user -> User.builder()
                .id(userId)
                .email(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail())
                .name(userDto.getName() == null ? user.getName() : userDto.getName())
                .build()).orElse(null);
    }

    public User mapToNewUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

}

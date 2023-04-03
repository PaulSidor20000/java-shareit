package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exceptions.ErrorHandler.FAILED_USER_ID;
import static ru.practicum.shareit.exceptions.ErrorHandler.DUPLICATED_EMAIL;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    public UserDto create(UserDto userDto) {
        User user = userDtoMapper.mapToNewUser(userDto);
        return userDtoMapper.mapToUserDto(java.util.Optional.of(userStorage.save(user)));
    }

    public UserDto read(Long userId) {
        if (userStorage.existsById(userId)) {
            return userDtoMapper.mapToUserDto(userStorage.findById(userId));
        }
        log.warn(String.format(FAILED_USER_ID, userId));
        throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
    }

    public UserDto update(Long userId, UserDto userDto) {
        if (!userStorage.existsById(userId)) {
            log.warn(String.format(FAILED_USER_ID, userId));
            throw new EntityNotFoundException(String.format(FAILED_USER_ID, userId));
        }
        userStorage.findUserByEmailContainingIgnoreCase(userDto.getEmail())
                .ifPresent(user -> {
                    if (!user.getId().equals(userId)) {
                        log.warn(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
                        throw new EmailDuplicateException(String.format(DUPLICATED_EMAIL, userDto.getEmail()));
                    }
                });
        User user = userDtoMapper.mapToUserModel(userId, userDto);
        return userDtoMapper.mapToUserDto(java.util.Optional.of(userStorage.save(user)));
    }

    public void delete(Long userId) {
        userStorage.deleteById(userId);
    }

    public Collection<UserDto> findAll() {
        List<User> users = new ArrayList<>();

        userStorage.findAll().forEach(users::add);
        return users.stream()
                .map((User aUser) -> userDtoMapper.mapToUserDto(Optional.ofNullable(aUser)))
                .collect(Collectors.toList());
    }

}
